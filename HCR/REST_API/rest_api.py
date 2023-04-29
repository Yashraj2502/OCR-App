from flask import Flask, jsonify, request
import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt
import os
from tensorflow.keras.layers.experimental.preprocessing import StringLookup

app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    # Get the image file from the request
    file = request.files['image']
    # Save the file to disk
    file.save('image.png')
    # Get the predicted text

    base_path = "dataset"
    words_list = []

    words = open(f"{base_path}\words.txt", "r").readlines()
    for line in words:
        if line[0] == "#":
            continue
        if line.split(" ")[1] != "err":  # We don't need to deal with errored entries.
            words_list.append(line)

    np.random.shuffle(words_list)
    
    split_idx = int(0.9 * len(words_list))
    train_samples = words_list[:split_idx]
    test_samples = words_list[split_idx:]

    val_split_idx = int(0.5 * len(test_samples))
    validation_samples = test_samples[:val_split_idx]
    test_samples = test_samples[val_split_idx:]

    assert len(words_list) == len(train_samples) + len(validation_samples) + len(
        test_samples
    )
    base_image_path = os.path.join(base_path, "data_subset")

    def get_image_paths_and_labels(samples):
        paths = []
        corrected_samples = []
        for (i, file_line) in enumerate(samples):
            line_split = file_line.strip()
            line_split = line_split.split(" ")

            # Each line split will have this format for the corresponding image:
            # part1/part1-part2/part1-part2-part3.png
            image_name = line_split[0]
            partI = image_name.split("-")[0]
            partII = image_name.split("-")[1]
            img_path = os.path.join(
                base_image_path, partI, partI + "-" + partII, image_name + ".png"
            )
            if os.path.getsize(img_path):
                paths.append(img_path)
                corrected_samples.append(file_line.split("\n")[0])

        return paths, corrected_samples

    train_img_paths, train_labels = get_image_paths_and_labels(train_samples)
    validation_img_paths, validation_labels = get_image_paths_and_labels(validation_samples)
    test_img_paths, test_labels = get_image_paths_and_labels(test_samples)

    train_labels_cleaned = []
    characters = set()
    max_len = 0

    for label in train_labels:
        label = label.split(" ")[-1].strip()
        for char in label:
            characters.add(char)

        max_len = max(max_len, len(label))
        train_labels_cleaned.append(label)

    characters = sorted(list(characters))
    
    # Load the saved model
    prediction_model = tf.keras.models.load_model(r'prediction_model')

    # Mapping characters to integers.
    char_to_num = StringLookup(vocabulary=list(characters), mask_token=None)

    # Mapping integers back to original characters.
    num_to_char = StringLookup(
        vocabulary=char_to_num.get_vocabulary(), mask_token=None, invert=True
    ) 
 
    def distortion_free_resize(image, img_size, resize_to=None):
        if resize_to is not None:
            image = tf.image.resize(image, resize_to, preserve_aspect_ratio=True , method=tf.image.ResizeMethod.AREA)
        w, h = img_size
        image = tf.image.resize(image, size=(h, w), preserve_aspect_ratio=True)

        # Check tha amount of padding needed to be done.
        pad_height = h - tf.shape(image)[0]
        pad_width = w - tf.shape(image)[1]

        # Only necessary if you want to do same amount of padding on both sides.
        if pad_height % 2 != 0:
            height = pad_height // 2
            pad_height_top = height + 1
            pad_height_bottom = height
        else:
            pad_height_top = pad_height_bottom = pad_height // 2

        if pad_width % 2 != 0:
            width = pad_width // 2
            pad_width_left = width + 1
            pad_width_right = width
        else:
            pad_width_left = pad_width_right = pad_width // 2

        image = tf.pad(
            image,
            paddings=[
                [pad_height_top, pad_height_bottom],
                [pad_width_left, pad_width_right],
                [0, 0],
            ],
        )

        image = tf.transpose(image, perm=[1, 0, 2])
        image = tf.image.flip_left_right(image)
        return image
    
    image_width = 128
    image_height = 32

    def preprocess_image(image_path, img_size=(image_width, image_height)):
        image = tf.io.read_file(image_path)
        image = tf.image.decode_png(image, 1)
        image = distortion_free_resize(image, img_size, resize_to=None)
        image = tf.cast(image, tf.float32) / 255.0
        return image

    def decode_predictions(pred):
        input_len = np.ones(pred.shape[0]) * pred.shape[1]
        results = tf.keras.backend.ctc_decode(pred, input_length=input_len, greedy=True)[0][0][:, :100]
        output_text = []
        for res in results:
            res = tf.gather(res, tf.where(tf.math.not_equal(res, -1)))
            res = tf.cast(res, dtype=tf.int64)  # Convert the tensor to int64 data type
            res = tf.strings.reduce_join(num_to_char(res)).numpy().decode("utf-8")
            output_text.append(res)
        return output_text
    
    def predict_text(image_path):
        # Preprocess the input image
        image = preprocess_image(image_path)
        # Add a batch dimension
        image = tf.expand_dims(image, axis=0)
        # Get the model's predictions
        pred = prediction_model.predict(image)
        # Decode the predictions
        text = decode_predictions(pred)[0]
        return text

    predicted_text = predict_text('image.png')
    # Return the predicted text as JSON response
    return jsonify({'text': predicted_text})

if __name__ == '__main__':
    app.run(debug=True)

import requests

# image_file = {'image': open(r'H:\Mini projects\ML project\New folder\Main\IAM_Dataset\Dataset\a01\a01-003\a01-003-00-04.png', 'rb')}
image_file = {'image': open(r'..\dataset\data_subset\a01\a01-003x\a01-003x-00-00.png', 'rb')}
response = requests.post('http://127.0.0.1:5000/predict', files=image_file)

if response.ok:
    recognized_text = response.json()['text']
    print(recognized_text)
else:
    print('Error:', response.json()['error'])

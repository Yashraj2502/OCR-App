package com.example.medicationapp2;

public class medicines {

    private String mName;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmUses() {
        return mUses;
    }

    public void setmUses(String mUses) {
        this.mUses = mUses;
    }

    public String getmSideEffect() {
        return mSideEffect;
    }

    public void setmSideEffect(String mSideEffect) {
        this.mSideEffect = mSideEffect;
    }

    public String getmBrand() {
        return mBrand;
    }

    public void setmBrand(String mBrand) {
        this.mBrand = mBrand;
    }

    private String mType;
    private String mUses;
    private String mSideEffect;
    private String mBrand;

    /**
     *
     * @param vName
     * @param vType
     * @param vUses
     * @param vSideEffect
     * @param vBrand
     */
    public medicines(String vName, String vType, String vUses, String vSideEffect, String vBrand){
        this.mName = vName;
        this.mType = vType;
        this.mBrand = vBrand;
        this.mSideEffect = vSideEffect;
        this.mUses = vUses;
    }

    /**
     *
     * @param vName
     */
    public medicines(String vName){
        this.mName = vName;
    }

}

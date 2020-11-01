package com.example.mymap;

public class SpinnerItem {
    private String itemName;
    private int itemImg;

    public SpinnerItem(String item,int imgItem){
        itemName = item;
        itemImg=imgItem;
    }

    public String getItmeName(){
        return itemName;
    }

    public int getImgItem(){
        return itemImg;
    }

}

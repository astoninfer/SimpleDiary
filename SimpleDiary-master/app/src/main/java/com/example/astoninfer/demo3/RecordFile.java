package com.example.astoninfer.demo3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

public class RecordFile {

    private String name;
    private LinkedList<String> tags;
    private LinkedList<RecordPart> content;

    public RecordFile() {
        content = new LinkedList<RecordPart>();
        tags = new LinkedList<String>();
    }

    public void setname(String s) {
        name = s;
    }

    public void addTag(String s) {
        tags.add(s);
    }

    public void removetag(int i) {
        if(i < tags.size()) {
            tags.remove(i);
        }
    }

    public LinkedList<String> gettags() {
        LinkedList<String> ts = new LinkedList<String>();
        for(int i = 0;i < tags.size();i ++) {
            ts.add(tags.get(i));
        }
        return ts;
    }

    public void addContent(int i,int type,char c,int size,int color,int special) {
        content.add(i,new RecordPart(type,c,size,color,special));
    }

    public void deleteContent(int i) {
        if(i < content.size()) {
            content.remove(i);
        }
    }

    public int[] getstyle(int i) {
        int[] style = new int[3];
        style[0] = content.get(i).type;
        style[1] = content.get(i).color;
        style[2] = content.get(i).size;
        return style;
    }

    public void savetofile(File file) {

        try {
            RecordPart rp;
            if(!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(content);
            fos.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }




}

class RecordPart implements Serializable {
    public int type;
    public char c;
    public int size;
    public int color;
    public int special;
    public RecordPart(int type,char c,int size,int color,int special) {
        this.type = type;
        this.color = color;
        this.c = c;
        this.size = size;
        this.special = special;
    }

}
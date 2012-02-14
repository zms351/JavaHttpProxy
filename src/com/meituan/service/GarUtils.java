package com.meituan.service;

import java.io.*;

public class GarUtils {

    public static int transfer(InputStream input,OutputStream output) throws IOException {
        return transfer(input,output,-1);
    }
    
    public static int transfer(InputStream input,OutputStream output,long maxLength) throws IOException {
        if(maxLength<0) {
            maxLength=Long.MAX_VALUE;
        }
        byte[] buffer=new byte[204800];
        int max=buffer.length;
        if(max>maxLength) {
            max= (int) maxLength;
        }
        int size;
        int total=0;
        while((size=input.read(buffer,0,max))>=0) {
            output.write(buffer,0,size);
            maxLength-=size;
            total+=size;
            if(maxLength<=0) {
                break;
            } else {
                if(max>maxLength) {
                    max= (int) maxLength;
                }
            }
        }
        return total;
    }

}

package com.example.runaction;

import java.io.InputStream;

import android.content.Context;

public class UseFile {
	public final static int CHIP_SIZE = 32;
    public static byte[][] load(Context context) {
        try {
            InputStream in = context.getResources().openRawResource(R.raw.map);
            int row = in.read();
            int col = in.read()<<8 | in.read();
            // マップサイズを設定
//            int width = col * CHIP_SIZE;
//            int height = row * CHIP_SIZE;
            // マップを作成
            byte[][] map = new byte[row][col];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    map[i][j] = (byte) in.read();
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

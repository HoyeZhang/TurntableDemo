package com.example.turntabledemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.turntabledemo.turntable.GiftBean;
import com.example.turntabledemo.turntable.TurntableView;

import java.util.ArrayList;
import java.util.Random;

/**
 * @Package: com.example.turntabledemo
 * @ClassName: MainActivity
 * @Description:
 * @Author: zhy
 * @CreateDate: 2021/5/26 17:58
 */
public class MainActivity extends Activity {
    private TurntableView turntableView;
    private TurnProgressView turnProgressView;
    private Random random;
    private float point;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        turntableView = findViewById(R.id.lrv);
        ArrayList<GiftBean> giftBeans = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            GiftBean giftBean = new GiftBean();
            giftBean.setGiftName("i= " + i);
            giftBean.setId(100 + i);
            giftBean.setGiftImageUrl("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2Fdfa9b51a0129d297bbcd9265aa278cada15c4c1e8d135-KNUWOY_fw658&refer=http%3A%2F%2Fhbimg.b0.upaiyun.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1624691592&t=b8b442f412b80e2cdc834ba51fb3a499");
            giftBeans.add(giftBean);
        }

        turntableView.setData(giftBeans);
        turntableView.setLuckyPosition(102);

        ImageView ivStart = findViewById(R.id.iv_start);
        random = new Random();
        ivStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turntableView.setLuckyPosition(100 + random.nextInt(7));
            }
        });

         turnProgressView = findViewById(R.id.tp_progress);
        turnProgressView.setPoint(point/100f);

        Button button1 = findViewById(R.id.add_1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(point <100) {
                    point++;
                    turnProgressView.startAnimal(point / 100f);
                }
            }
        });
        Button button10 = findViewById(R.id.add_10);
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                point = (point +10);
                if(point >=100){
                    point = point%100;
                    turnProgressView.startAnimal(point/100f);
                }else {
                    turnProgressView.startAnimal(point/100f);
                }

            }
        });
        Button button30 = findViewById(R.id.add_30);
        button30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                point = point +30;
                if(point >=100){
                    point = point%100;
                    turnProgressView.startAnimal(point/100f);
                }else {
                    turnProgressView.startAnimal(point/100f);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}

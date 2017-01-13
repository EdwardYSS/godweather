package edward.godweather;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import edward.godweather.address.Address;
import edward.godweather.service.NetWorkStateService;
import edward.godweather.util.AcyivityCollector;
import edward.godweather.util.NetworkUtils;

public class WelActivity extends AppCompatActivity {

    private ImageView wei_img;
    private ObjectAnimator animator;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wel);
        context = this;
        initView();
    }

    private void initView() {

        //启动网络监听服务
        startService(new Intent(this, NetWorkStateService.class));

        wei_img = (ImageView) findViewById(R.id.wei_img);
        PropertyValuesHolder pv1 = PropertyValuesHolder.ofFloat("alpha",0.1f,1.0f);
        PropertyValuesHolder pv2 = PropertyValuesHolder.ofFloat("rotation",0.0f,359.0f);
        animator = ObjectAnimator.ofPropertyValuesHolder(wei_img,pv1,pv2);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(3000L);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(context == null) {
                    return;
                }

                if(!NetworkUtils.isHaveNetWork) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("为检测到网络");
                    builder.setMessage("是否去设置网络");
                    builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //设置网络
                            dialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivityForResult(intent, Address.QUEST_CODE_SETTING_NETWORK);
                        }
                    });
                    builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //退出
                            dialog.dismiss();
                            AcyivityCollector.finishAll();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    //跳转到主页面
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Address.QUEST_CODE_SETTING_NETWORK) {
            initView();
        }
    }

    @Override
    public void onBackPressed() {
        //处理返回键
        stopService(new Intent(context, NetWorkStateService.class));
        super.onBackPressed();
    }
}

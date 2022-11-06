package cc.hicore.qtool.GroupChecker.CheckAlive;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;

import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.UIViews.MyTimePicker;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.QQManager.QQGroupManager;
import cc.hicore.qtool.QQManager.QQGroupUtils;

public class CheckCommon {
    public static void CollectAndCheck(String GroupUin) {
        Activity act = Utils.getTopActivity();
        ResUtils.StartInject(act);
        MyTimePicker picker = new MyTimePicker(act, 30, 0, 0, 0, false);
        new AlertDialog.Builder(act, 3)
                .setTitle("设置检测的时间")
                .setView(picker)
                .setNegativeButton("开始检测", (dialog, which) -> {
                    ProgressDialog progress = new ProgressDialog(act, 3);
                    progress.setTitle("请稍后..");
                    progress.setMessage("正在刷新群成员列表..");
                    progress.setCancelable(false);
                    progress.show();
                    new Thread(() -> {
                        try {
                            CollectAndAnalyse(GroupUin, act, picker.GetSecond() * 1000L);
                        } finally {
                            Utils.PostToMain(progress::dismiss);
                        }
                    }).start();
                }).show();
    }

    private static void CollectAndAnalyse(String GroupUin, Context context, long time) {
        ArrayList<QQGroupUtils.GroupMemberInfo> member = QQGroupUtils.waitForGetGroupInfo(GroupUin);
        Utils.PostToMain(() -> ShowResult(GroupUin, context, member, time));
    }

    @SuppressLint("ResourceType")
    private static void ShowResult(String GroupUin, Context context, ArrayList<QQGroupUtils.GroupMemberInfo> memberInfo, long time) {
        RelativeLayout mRoot = new RelativeLayout(context);
        AlertDialog dialog = new AlertDialog.Builder(context, 3)
                .setTitle("检测结果")
                .setView(mRoot)
                .create();


        LinearLayout toolBar = new LinearLayout(context);
        toolBar.setId(778899);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mRoot.addView(toolBar, param);

        ArrayList<CheckBox> checkBoxes = new ArrayList<>();

        Button btnSelectBack = new Button(context);
        btnSelectBack.setText("反选");
        toolBar.addView(btnSelectBack);
        btnSelectBack.setOnClickListener(v -> {
            for (CheckBox ch : checkBoxes) ch.setChecked(!ch.isChecked());
        });
        Button btnKickAll = new Button(context);
        btnKickAll.setText("踢出选中");
        toolBar.addView(btnKickAll);
        btnKickAll.setOnClickListener(v -> {
            ArrayList<String> kickUin = new ArrayList<>();
            for (CheckBox ch : checkBoxes) {
                if (ch.isChecked()) {
                    kickUin.add((String) ch.getTag());
                }
            }
            QQGroupManager.Group_Kick(GroupUin, kickUin.toArray(new String[0]), false);
            Utils.ShowToast("已提交踢出请求");
            dialog.dismiss();
        });

        ScrollView sc = new ScrollView(context);
        LinearLayout mList = new LinearLayout(context);
        mList.setOrientation(LinearLayout.VERTICAL);
        sc.addView(mList);

        for (QQGroupUtils.GroupMemberInfo info : memberInfo) {
            if (System.currentTimeMillis() - info.last_active * 1000 > time) {
                CheckBox checkBox = new CheckBox(context);
                checkBox.setTag(info.Uin);
                StringBuilder builder = new StringBuilder();
                builder.append("[LV").append(info.level).append("]");
                builder.append(info.Name).append("(").append(info.Uin).append(")");
                builder.append("[").append(Utils.secondToTime((System.currentTimeMillis() - info.last_active * 1000) / 1000)).append("]");
                checkBox.setText(builder);
                checkBox.setTextColor(Color.BLACK);
                mList.addView(checkBox);
                checkBoxes.add(checkBox);
            }
        }

        param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        param.addRule(RelativeLayout.BELOW, 778899);
        param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        mRoot.addView(sc, param);

        dialog.show();

    }
}

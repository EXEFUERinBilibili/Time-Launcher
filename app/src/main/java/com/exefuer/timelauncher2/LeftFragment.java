package com.exefuer.timelauncher2;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class LeftFragment extends Fragment {

    private View rootView;
    private AudioManager audioManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_control_center, container, false);

        // 初始化音频管理器
        audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);

        setupEventListeners();

        return rootView;
    }

    private void setupEventListeners() {
        // Wi-Fi 按钮点击事件
        rootView.findViewById(R.id.wlanBtn).setOnClickListener(v ->
                openSystemSettings(Settings.ACTION_WIFI_SETTINGS, "WiFi")
        );

        // 蓝牙按钮点击事件
        rootView.findViewById(R.id.BtBtn).setOnClickListener(v ->
                openSystemSettings(Settings.ACTION_BLUETOOTH_SETTINGS, "蓝牙")
        );

        // 设置按钮点击事件
        rootView.findViewById(R.id.settingsBtn).setOnClickListener(v ->
                openAppSettings()
        );

        // 亮度按钮点击事件
        rootView.findViewById(R.id.brightnessBtn).setOnClickListener(v ->
                openBrightnessSettings()
        );

        // 音量按钮点击事件
        rootView.findViewById(R.id.volumeBtn).setOnClickListener(v ->
                adjustVolume()
        );

        rootView.findViewById(R.id.data).setOnClickListener(v ->
                opendataSettings()
        );
    }

    private void opendataSettings() {
        try {
            // 尝试打开亮度设置
            Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            showToast("无法打开亮度设置");
        }
    }

    private void openSystemSettings(String action, String settingName) {
        try {
            startActivity(new Intent(action));
        } catch (Exception e) {
            showToast("无法打开" + settingName + "设置");
        }
    }

    private void openAppSettings() {
        try {
            startActivity(new Intent(requireActivity(), activity_settings.class));
        } catch (Exception e) {
            showToast("无法打开应用设置");
        }
    }

    private void openBrightnessSettings() {
        try {
            // 尝试打开亮度设置
            Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            showToast("无法打开亮度设置");
        }
    }

    private void adjustVolume() {
        try {
            // 显示音量控制面板
            Intent intent = new Intent(Intent.ACTION_VOICE_COMMAND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            // 如果语音命令不可用，尝试直接调整音量
            try {
                // 增加音量
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI
                );
            } catch (Exception ex) {
                showToast("无法调整音量");
            }
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 当Fragment恢复时，可以在这里添加其他刷新逻辑
    }
}
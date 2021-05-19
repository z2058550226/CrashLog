# CrashLogViewer

此工具用于查看手机内所有app的各种崩溃（crash/anr/watchdog）。安装后需要给予两个系统权限：
```bash
adb shell pm grant com.bybutter.crashlog android.permission.READ_LOGS

adb shell pm grant com.bybutter.crashlog android.permission.PACKAGE_USAGE_STATS
```
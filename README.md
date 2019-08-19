# TB4License
A license manager for TrackBe4

主要命令：
//首先要用KeyTool工具来生成私匙库：（-alias别名 –validity 3650表示10年有效）
keytool -genkey -alias maxtree -keystore MaxtreeKeys.store -validity 3650

//然后把私匙库内的公匙导出到一个文件当中：
keytool -export -alias maxtree -file certfile.cer -keystore MaxtreeKeys.store

//然后再把这个证书文件导入到公匙库：
keytool -import -alias publiccert -file certfile.cer -keystore publicCerts.store    // 注意需要改下密码



当前各个密钥的密码：
MaxtreeKeys.store,密码Happy2016
maxtree，密码Eagleflyhigh2018
publicCerts.store, 密码Wonderful2018


//============================================================
打包命令：
mvn assembly:assembly

// 运行主类
com.maxtree.tb4license.server.App


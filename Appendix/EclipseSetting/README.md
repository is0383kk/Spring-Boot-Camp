# Eclipseの導入（Ubuntu20.04）  
1. [ここ](http://www.eclipse.org/downloads/)からEclipseのダウンロード  
    - Download x86_68のボタンから「eclipse-inst-jre-linux64.tar.gz」をダウンロード
2. 「eclipse-inst-jre-linux64.tar.gz」を解凍し、「eclipse-inst」をダブルクリックするとインストーラが起動 

## Eclipseの日本語化
[ここ](https://mergedoc.osdn.jp/)からPleiadesプラグインのダウンロードを行う  

「/home/ユーザ名/eclipse/jee-2022-12/eclipse/plugins/」に「plugins/jp.sourceforge.mergedoc.pleiades」を格納する(マージ)
eclipse.iniに以下を追記する

```
-javaagent:/home/ユーザ名/eclipse/jee-2022-12/eclipse/plugins/jp.sourceforge.mergedoc.pleiades/pleiades.jar
-Xverify:none
```

# Eclipseで「Spring Bootプロジェクト」の作成ができるようにする  
1. Spring Tool Suite（STS）のインストール（Fullバージョンの場合インストール済み）  
  - Eclipseの「ヘルプ」->「マーケットプレイス」->「Spring Tool Suite」を検索してインストール  
2. Spring スターター・プロジェクトが作成できるかの確認  
「新規プロジェクト」から「Springスタータープロジェクト」を選択できたらOK

# Spring スタータプロジェクトを作る
## プロジェクトの作成
新規プロジェクトからSpringスタータープロジェクトを選択し作成する  
- 「新規Springスタータ・プロジェクト」での画面
  - 「名前」と「成果物」と「パッケージ」の名称を一致させる（させなくていい可能性あり）
  - タイプを「Gradle-Groovy」
  - 「パッケージング」は一応war
- 「新規Springスタータ・プロジェクト依存関係」画面
  - Lombok
  - Spring Web
  - MySQL Driver
  - My Batis Framework
  - Thymeleaf
ここまででプロジェクトが作成されていればOK  

## 設定ファイル
「build.gradle」に「新規Springスタータ・プロジェクト依存関係」画面で設定した機能の情報が記述されている  
※もし追加する場合はプロジェクトを右クリック「Spring」->「スターターの追加」から追加できる  
- DB接続情報を「application.property」に以下を記述
```
spring.datasource.url=jdbc:mysql://localhost:8080/test
spring.datasource.username=root
spring.datasource.password=パスワード
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```
ここで「Springアプリケーション」で実行してエラーが出なければOK  

## 実行時に表示されるHTMLファイルを作成する  
- 「/src/main/resources/templates」にファイルを作成し以下の「index.html」を作成する  
```html
<!DOCTYPE html>
<html>
  <head>
    <title>Hello</title>
    <meta charset="utf-8" />
  </head>
  <body>
    <h1>Hello SpringBoot Sample!</h1>
  </body>
</html>

```
## Controllerを作成する  
- 「/src/main/java」にクラスを作成し以下の「HelloController」を作成する  
```java
package com.example.springSample;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ComponentScan(basePackageClasses = HelloController.class)
@Controller
public class HelloController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        return "index";  //表示するHTMLファイルの名前（拡張子不要）を指定
    }       
}
```
再度「Springアプリケーション」で実行してエラーが出なければOK 

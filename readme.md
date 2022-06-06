g[![pipeline status](https://gitlab.com/betterdaysfps/spigot-client/badges/develop/pipeline.svg)](https://gitlab.com/betterdaysfps/spigot-client/commits/develop)

# spigot-client

このプロジェクトはbetterdaysfps用に開発・保守しています。
当サーバの開発者以外へのコードの流用行為を禁じます。

## 開発環境

[Guide](https://gitlab.com/betterdaysfps/Guide)にプラスして以下を定めます。


kotlin: 1.2.61   
Spigotバージョン: 1.12

## コーディング規則
[こちら](https://gitlab.com/betterdaysfps/Guide/blob/master/Kotlin-Coding-Style.md)を規則とします。　　(規則制定前から開発を始めていたため、数か所、違反している箇所がありますが今後修正していく所存です。)
Javaのコードについてですが今後Kotlinに移行するため、現時点では定めません。


## コミット
箇条書きで詳しく書いてください。
コミット種別を必ずprefixとしてください。

fix：バグ修正  
hotfix：クリティカルなバグ修正  
add：新規（ファイル）機能追加  
update：機能修正（バグではない）  
change：仕様変更  
clean：整理（リファクタリング等）   
disable：無効化（コメントアウト等）    
remove：削除（ファイル）  
upgrade：バージョンアップ
revert：変更取り消し  

```
ex. FIX: 武器が購入できないバグを修正し、失敗した場合はプレイヤーにメッセージを送信するように

```

複数ある場合にはリスト化してください

## ブランチ
Gitflowを擬似化し運用していきます。  
master - 本番環境  
develop - 開発環境 (リリース時にmasterへマージ)  
release branches - 機能の追加、バグ修正 (developにマージ)



## ブランチの作成・マージ
基本的に個人での修正の場合は個人名をブランチ名にしてください。
何らかの目的で新規でブランチが必要になった場合や集団で使用する場合は目的に沿うように命名してください。

マージリクエストする際はdevelopブランチにを宛先としてください(ブランチが存在しない場合は作成)
develop環境で検証が完了し、本番環境に適用する際はmasterブランチにマージします。


## ソースコードの変更
変更権限は未然にトラブルやバグを防ぐため開発班(Developer)のみとします。
ソースコードの修正後は追加・変更理由の詳細を添付しdevelopへマージリクエストを送信してください。

## アイディア・バグ等
プロジェクトのIssueを使用することにします。また報告する際は適正なタグ付けを行ってください。
アイディアの審議、バグの原因が分かり次第、担当者を決め、その後は責任をもって作業を行ってください。作業は「ソースコードの変更」に基づきます。
運営Discordでソースコードに対する報告はチャットを汚しかねないため禁じます。
 



# Bean Validationで定義されているアノテーション

## Null・空文字・boolean
|アノテーション名|制約|データ型|要素|
|---|---|---|---|
|@Notnull|null以外|全て|-|
|@Null|null|全て|-|
|@NotEmpty|以下は不可 <br> null <br> 空|java.lang.charSequence <br> java.util.Collection <br> java.util.Map|-|
|@NotBlank|以下は不可 <br> null <br> 空文字 <br> 半角スペース|java.lang.CharSequence|-|
|@AssertTrue|true|boolean, Boolean|-|
|@AssertFalse|false|boolean, Boolean|-|


## 文字列長・数値・メールアドレス
|アノテーション名|制約|データ型|要素|
|---|---|---|---|
|@Size|長さがmin以上max以下|java.lang.CharSequence <br> java.util.Collection <br> java.util.Map|max：214748364 <br> min：0|
|@Pattern|regexpで指定した正規表現|java.lang.CharSequence|regexp：正規表現 <br> flags：|
|@Digits|小数部の桁数がfraction以下で整数部の桁数がinteger以下|java.math.BigDecimal <br> java.math.BigInteger <br> java.lang.CharSequence <br> byte・short・int・long <br> ラッパークラス|fraction：小数部の桁数の最大値 <br> integer：整数部の桁数の最大値|
|@DecimalMax|数値がvalue以下|java.math.BigDecimal <br> java.math.BigInteger <br> java.lang.CharSequence <br> byte・short・int・long <br> ラッパークラス|value：最大値 <br> inclusive：valueの値を含むか（falseだと含まない）|
|@DecimalMin|数値がvalue以上|java.math.BigDecimal <br> java.math.BigInteger <br> java.lang.CharSequence <br> byte・short・int・long <br> ラッパークラス|value：最小値 <br> inclusive：valueの値を含むか（falseだと含まない）|
|@Max|数値がvalue以下|java.math.BigDecimal <br> java.math.BigInteger <br> byte・short・int・long <br> ラッパークラス|value：最大値（long）|
|@Min|数値がvalue以上|java.math.BigDecimal <br> java.math.BigInteger <br> byte・short・int・long <br> ラッパークラス|value：最小値（long）|
|@Positive|数値が正|java.math.BigDecimal <br> java.math.BigInteger <br> byte・short・int・long <br> ラッパークラス|-|
|@PositiveOrZero|数値が正あるいは0|java.math.BigDecimal <br> java.math.BigInteger <br> byte・short・int・long <br> ラッパークラス|-|
|@Negative|数値が負|java.math.BigDecimal <br> java.math.BigInteger <br> byte・short・int・long <br> ラッパークラス|-|
|@NegativeOrZero|数値が負あるいは0|java.math.BigDecimal <br> java.math.BigInteger <br> byte・short・int・long <br> ラッパークラス|-|
|@Email|メールアドレス形式|java.lang.CharSequence|regexp：正規表現（オプション指定）|

## 日時系
|アノテーション名|制約|データ型|要素|
|---|---|---|---|
|@Past|日時が現在より過去|java.util.Data <br> java.util.Calendar <br> java.time.* 日時クラス|-|
|@PastOrPresent|日時が現在より過去あるいは現在|java.util.Data <br> java.util.Calendar <br> java.time.* 日時クラス|-|
|@Future|日時が現在より未来|java.util.Data <br> java.util.Calendar <br> java.time.* 日時クラス|-|
|@FutureOrPresent|日時が現在より未来あるいは現在|java.util.Data <br> java.util.Calendar <br> java.time.* 日時クラス|-|

## Hibernate Validator独自
|アノテーション名|制約|データ型|要素|
|---|---|---|---|
|@Range|数値がmin以上max以下|数値または文字列|max：最大値 <br> min：最小値|
|@Length|文字列長がmin以上max以下|java.lang.CharSequence|max：最大値 <br> min：最小値|
|@CodePointLength|文字列長がmin以上max以下 <br> サロゲートペアも対象|java.lang.CharSequence|max：最大値 <br> min：最小値 <br> normalizationStrategy|

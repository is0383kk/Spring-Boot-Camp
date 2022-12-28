# Bean Validationで定義されているアノテーション

|アノテーション名|制約|データ型|要素|
|---|---|---|---|
|@Notnull|nullは不可|全て|-|
|@Null|nullのみ可能|全て|-|
|@NotEmpty|以下は不可 <br> null <br> 空|java.lang.charSequence <br> java.util.Collection <br> java.util.Map|-|
|@NotBlank|以下は不可 <br> null <br> 空文字 <br> 半角スペース|java.lang.CharSequence|-|
|@AssertTrue|Trueのみ可能|boolean, Boolean|-|
|@AssertFalse|Falseのみ可能|boolean, Boolean|-|
|@Size|min以上max以下が可能|java.lang.CharSequence <br> java.util.Collection <br> java.util.Map|max：214748364 <br> min：0|
|@Pattern|regexpで指定した正規表現のみ可能|java.lang.CharSequence|regexp：正規表現 <br> flags：|
|@Email|メールアドレスのみ可能|java.lang.CharSequence|regexp：正規表現（オプション指定）|
|@Digits|小数部の桁数がfraction以下で整数部の桁数がinteger以下のみ可能|java.math.BigDecimal <br> java.math.BigInteger <br> java.lang.CharSequence <br> byte・short・int・long <br> ラッパークラス|fraction：小数部の桁数の最大値 <br> integer：整数部の桁数の最大値|
|@DecimalMax|数値がvalue以下のみ可能|java.math.BigDecimal <br> java.math.BigInteger <br> java.lang.CharSequence <br> byte・short・int・long <br> ラッパークラス|value：最大値 <br> inclusive：valueの値を含むか（falseだと含まない）|
|@DecimalMin|数値がvalue以上のみ可能|java.math.BigDecimal <br> java.math.BigInteger <br> java.lang.CharSequence <br> byte・short・int・long <br> ラッパークラス|value：最小値 <br> inclusive：valueの値を含むか（falseだと含まない）|

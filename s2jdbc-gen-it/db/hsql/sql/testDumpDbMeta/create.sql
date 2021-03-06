-- supported
create table integer_table (integer_column integer);
create table int_table (int_column int);
create table double_table (double_column double);
create table double_precision_table (double_precision_column double precision);
create table float_table (float_column float);
create table varchar_table (varchar_column varchar);
create table varchar_ignorecase_table (varchar_ignorecase_column varchar_ignorecase);
create table char_table (char_column char);
create table character_table (character_column character);
create table longvarchar_table (longvarchar_column longvarchar);
create table date_table (date_column date);
create table time_table (time_column time);
create table timestamp_table (timestamp_column timestamp);
create table datetime_table (datetime_column datetime);
create table decimal_table (decimal_column decimal);
create table numeric_table (numeric_column numeric);
create table boolean_table (boolean_column boolean);
create table bit_table (bit_column bit);
create table tinyint_table (tinyint_column tinyint);
create table smallint_table (smallint_column smallint);
create table bigint_table (bigint_column bigint);
create table real_table (real_column real);
create table varbinary_table (varbinary_column varbinary);
create table longvarbinary_table (longvarbinary_column longvarbinary);

-- unsupported
create table other_table (other_column other);
create table object_table (object_column object);
-- supported
create table binary_double_table (binary_double_column binary_double);
create table binary_float_table (binary_float_column binary_float);
create table blob_table (blob_column blob);
create table char_table (char_column char(1));
create table clob_table (clob_column clob);
create table date_table (date_column date);
create table float_table (float_column float);
create table long_table (long_column long);
create table long_raw_table (long_raw_column long raw);
create table nchar_table (nchar_column nchar(1));
create table number_table (number_column number(10,5));
create table numeric_table (numeric_column number(10,5));
create table decimal_table (decimal_column number(10,5));
create table nvarchar2_table (nvarchar2_column nvarchar2(2000));
create table raw_table (raw_column raw(2000));
create table timestamp_table (timestamp_column timestamp);
create table varchar2_table (varchar2_column varchar2(4000));

-- unsupported
create table urowid_table (urowid_column urowid (4000));

CREATE OR REPLACE FUNCTION FUNC_SIMPLETYPE_PARAM
( param1 IN NUMBER )
RETURN NUMBER
AS
BEGIN
  RETURN 20;
END FUNC_SIMPLETYPE_PARAM;
/


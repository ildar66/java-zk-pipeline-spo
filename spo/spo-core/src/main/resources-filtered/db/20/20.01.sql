--liquibase formatted sql
CREATE OR REPLACE FUNCTION "GET_CURRENCY_RATE" (currencyName VARCHAR2) return NUMBER is
  rate NUMBER;
begin
  rate := -1;

  begin
        SELECT CURR_RATE into rate FROM (SELECT CR.RATE CURR_RATE
        FROM CRM_FB_EXCHANGERATE CR
        WHERE CR.CURRENCYCODE = currencyName
        ORDER BY CR.ACTIVEDATE DESC)
        WHERE ROWNUM = 1;
  end;

  return(rate);
end GET_CURRENCY_RATE;
/
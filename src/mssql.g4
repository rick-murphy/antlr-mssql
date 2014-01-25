grammar mssql;

fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');

/******************** Key Words ********************/
SELECT : S E L E C T ;
FROM : F R O M ;
WHERE : W H E R E ;
CASE : C A S E ;
WHEN : W H E N ;
THEN : T H E N ;
ELSE : E L S E ;
END : E N D ;
GROUP : G R O U P ;
BY : B Y ;
AS : A S ;
INNER : I N N E R ;
LEFT : L E F T ;
OUTER : O U T E R ;
JOIN : J O I N ;
ON : O N ;
IN : I N ;
LIKE : L I K E ;
IS : I S ;
NOT : N O T ;
NULL : N U L L ;
AND : A N D ;
OR : O R ;
/****************************************/
WS : [ \t\r\n] -> skip ;
COMMENT : '-' '-' ~('\r'|'\n')* '\r'? '\n' -> skip;
DOT : '.' ;
COMMA : ',' ;
LB : '(' ;
RB : ')' ;
LSB : '[' ;
RSB : ']' ;
DIGIT : [0-9]+ ;
ID : [a-zA-Z]
	| [a-zA-Z][0-9a-zA-Z_]+
	| LSB ID RSB
	;
string : '\'' ('%')? ~'\''* ('%')? '\'' ;
number : DIGIT 
	| DIGIT DOT DIGIT
	| '-' number
	;
path : ID (DOT ID)+;

field : expr ((AS)? ID)? ;
fieldlist : field
	| field (COMMA field)* 
	;

table : (path|ID) ((AS)? ID)?
	| LB select RB (AS)? ID
	;
tablejoin : (LEFT (OUTER)? JOIN|INNER JOIN) table (tablejoin)* ON stmt ;
tablelist : table (tablejoin)*
	| table (COMMA table)*
	| table
	;
var : casestmt
	| funcstmt
	| LB string (COMMA string)* RB
	| LB var RB
	| path
	| string
	| number
	| ID
	;
expr :  LB select RB
	| expr ('*'|'/') expr
	| expr ('+'|'-') expr
	| LB expr RB
	| '-' expr
	| var
	;
condition : expr ('>='|'<='|'<>'|'>'|'<'|'=') expr
	| expr IN LB select RB
	| expr IN LB string (COMMA string)* RB
	| expr LIKE string
	| var IS (NOT)? NULL
	;
stmt : stmt AND stmt
	| stmt OR stmt
	| LB stmt RB
	| condition
	;
casestmt : CASE (var)? (WHEN stmt THEN expr)+ (ELSE expr)? END ;
funcstmt : (path|ID) LB expr (COMMA expr)* RB ;
select : SELECT fieldlist FROM tablelist (WHERE stmt)? (GROUP BY (path|ID) (COMMA (path|ID))*)? ;
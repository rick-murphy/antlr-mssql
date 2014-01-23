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
AS : A S ;
INNER : I N N E R ;
LEFT : L E F T ;
OUTER : O U T E R ;
JOIN : J O I N ;
ON : O N ;
IN : I N ;
LIKE : L I K E ;
IS : I S ;
NULL : N U L L ;
/****************************************/
WS : [ \t\r\n] -> skip ;
DOT : '.' ;
MINUS : '-' ;
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
	
oph : ('*'|'/') ;
opl : ('+'|'-') ;
bop : (LIKE|IN|'>='|'<='|'<>'|'>'|'<'|'=') ;
string : '\'' ~'\''* '\'' ;
number : DIGIT 
	| DIGIT DOT DIGIT
	| MINUS number
	;

field : expr ((AS)? ID)? ;
fieldlist : field
	| field (COMMA field)* 
	;

table : ID (DOT ID)* ((AS)? ID)?
	| LB select RB (AS)? ID
	;
tablejoin : (LEFT (OUTER)? JOIN|INNER JOIN) table (tablejoin)* ON stmt ;
tablelist : table
	| table (COMMA table)*
	| table (tablejoin)*
	;
var : ID
	| number
	| ID (DOT ID)*
	| LB string (COMMA string)* RB
	| LB var RB
	;
expr : var
	| LB expr RB
	| expr (oph|opl) expr
	;
stmt : ID ;
select : SELECT fieldlist FROM tablelist ;
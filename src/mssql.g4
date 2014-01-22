grammar mssql;

ID : [\[\]0-9a-zA-Z_]+ ;
WS : [ \t] -> skip ;
COMMENT : '-' '-' ~('\r'|'\n')* '\r'? '\n' -> skip;


op : '=' | '>' | '>=' | '<' | '<=' | 'like' | '+' | '-' | '*' | '/' | 'in' ;
bop : 'and' | 'or' ;

select : 'select' fieldlist 'from' tablelist ('where' stmt)?
	| '(' select ')';

field : '*'
	| ID
	| ID 'as' ID
	| ID ID
	| ID '.' ID
	| ID '.' ID 'as' ID
	| ID '.' ID ID
	| var ID
	| var 'as' ID 
	| stmt ID
	| stmt 'as' ID
	;
	
fieldlist : field (',' field)* ;

table : ID
	| ID 'as' ID
	| ID ID
	| '(' select ')' ID
	| '(' select ')' 'as' ID
	;
	
tablelist : table (',' table)* 
	| table (tablejoin)*
	;
	
tablejoin : 'left' ('outer')? 'join' table (tablejoin)* 'on' stmt 
	| 'inner' 'join' table (tablejoin)* 'on' stmt;

string : '\'' ~'\''* '\'' ;

var : ID
	| ID '.' ID
	| '-' var
	| '(' var ')'
	;
	
stmt : var 
	| string
	| string (',' string)*
	| select
	| casestmt
	| funcstmt
	| stmt op stmt
	| var 'is' ('not')? 'null'
	| stmt bop stmt
	| '(' stmt ')'
	| '-' stmt
	;

casestmt : 'case' ID? ('when' stmt 'then' (stmt|select))+ ('else' (stmt|select))? 'end' ;
	
funcstmt : ID ('.' ID)? '(' funcparams ')' ;

funcparams : var
	| var (',' var)*
	;
	

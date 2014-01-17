grammar mssql;

ID : [\[\]0-9a-zA-Z_]+ ;
WS : [ \t] -> skip ;
COMMENT : '-' '-' ~('\r'|'\n')* '\r'? '\n' -> skip;

op : '=' | '>' | '>=' | '<' | '<=' | 'like' | '+' | '-' | '*' | '/' ;
bop : 'and' | 'or' ;

select : 'select' fieldlist 'from' tablelist ('where' stmt)?;

field : '*'
	| ID
	| ID 'as' ID
	| ID ID
	| ID '.' ID
	| ID '.' ID 'as' ID
	| ID '.' ID ID
	| var ID
	| var 'as' ID 
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

var : '\'' ~'\''* '\'' 
	| ID
	| ID '.' ID
	| casestmt
	| funcstmt
	| '(' var ')'
	;
	
stmt : var 
	|var op var
	| stmt bop stmt
	| '(' stmt ')'
	;

casestmt : 'case' ID? ('when' stmt 'then' stmt)+ ('else' stmt)? 'end' ;
	
funcstmt : ID ('.' ID)? '(' funcparams ')' ;

funcparams : ID
	| ID (',' ID)*
	;
	

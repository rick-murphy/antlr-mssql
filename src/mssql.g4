grammar mssql;

ID : [0-9a-zA-Z_]+ ;
WS : [ \t] -> skip ;

op : '=' | '>' | '>=' | '<' | '<=' | 'like' ;
bop : 'and' | 'or' ;

select : 'select' fieldlist 'from' tablelist 'where' stmt ;

field : '*'
	| ID
	| ID 'as' ID
	| ID ID
	| ID '.' ID
	| ID '.' ID 'as' ID
	| ID '.' ID ID
	;
	
fieldlist : field (',' field)* ;

table : ID
	| ID 'as' ID
	| ID ID
	| '(' select ')' ID
	| '(' select ')' 'as' ID
	;
	
tablelist : table (',' table)* ;

expr : '\'' ~'\''* '\'' 
	| ID
	| ID '.' ID
	| '(' expr ')'
	;
	
stmt : expr op expr
	| stmt bop stmt
	;
	

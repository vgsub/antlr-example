grammar Example;

@header {
package ru.tinkoff.tma.caen.el;
}

// Parser rules
parse: expression*;

expression
  : LBRACKET expression RBRACKET        #parExpression
  | expression AND expression           #andExpression
  | expression OR expression            #orExpression
  | NOT expression                      #notExpression
  | method                              #methodExpression
  ;

method: object=(PLACEHOLDER | STRING) '.' methodName LBRACKET methodArguments RBRACKET;
methodName: TEXT;
methodArguments: (PLACEHOLDER | INT | STRING)+;


// Lexer rules
NOT: '!';
AND: '&&';
OR: '||';
LBRACKET : '(';
RBRACKET : ')';
SHARP: '#';
PLACEHOLDER: SHARP TEXT;

// literals
INT: [0-9]+;
STRING: '"' (~["\r\n] | '""')* '"';

TEXT: [a-zA-Z_0-9]+;

SPACE: [ \t\r\n] -> skip;
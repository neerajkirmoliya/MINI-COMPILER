grammar Expr;

prog:   expr EOF;

expr:   expr op=('+'|'-') term   # AddSub
    |   term                     # ToTerm
    ;

term:   term op=('*'|'/') factor # MulDiv
    |   factor                   # ToFactor
    ;

factor: INT                      # Int
      | '(' expr ')'             # Parens
      ;

INT :   [0-9]+ ;
WS  :   [ \t\r\n]+ -> skip ;
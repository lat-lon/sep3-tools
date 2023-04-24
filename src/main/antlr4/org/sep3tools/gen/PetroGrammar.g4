grammar PetroGrammar;

schichtbeschreibung: bestandteile;

bestandteile:
    bestandteil                                                     # Teil
    | bestandteile ',' bestandteile                                 # Aufzaehlung_b
    | '(' bestandteile ',' bestandteile ')' ( '(' attribute ')' )?  # Aufzaehlung_b_k
    | uebergang_bes                                                 # Uebergang_b
;

uebergang_bes:
    b1=bestandteil  '-' b2=bestandteil
    | '(' uebergang_bes ')' ( '(' attribute ')' )?
;

bestandteil:
    TEIL ( '(' attribute ')' )?
    | '(' TEIL  ( '(' attribute ')' )? ')';

attribute:
    attribut                                    # att
    | uebergang_att                             # Uebergang_a
    | attr=attribute '(' unter=attribute ')'    # unter_Attribute
    | attribute ',' attribute                   # Aufzaehlung_a
;
uebergang_att: attribut '-' attribut;

attribut:
    TEIL                # attr
    | attribut FRAGLICH # attr_fraglich
    | attribut SICHER   # attr_sicher
    | TIEFE             # attr_tiefe
    ;

TIEFE: ([0-9]|'.')+;
TEIL: ANY+;
UNBEKANNT: ANY+;
ANY: [a-z]|[A-Z]|[0-9]|'^'|'*'|'+'|'"';
FRAGLICH: '?';
SICHER: '!';
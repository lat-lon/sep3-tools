grammar PetroGrammar;

schichtbeschreibung: bestandteile;

bestandteile:
    bestandteil ( '(' attribute ')' )?    # Teil
    | bestandteile ',' bestandteile     # Aufzaehlung_b
    | uebergang_bes ( '(' attribute ')' )?  # Uebergang_b

;

uebergang_bes: b1=bestandteil '-' b2=bestandteil;

bestandteil: TEIL;

attribute:
    attribut                                    # att
    | uebergang_att                             # Uebergang_a
    | attr=attribute '(' unter=attribute ')'    # unter_Attribute
    | attribute ',' attribute                  # Aufzaehlung_a
;
uebergang_att: attribut '-' attribut;

attribut:
    ATTRIBUT            # attr
    | UNBEKANNT         # attr_unbek
    | attribut FRAGLICH # attr_fraglich
    | attribut SICHER   # attr_sicher
    ;

ATTRIBUT:
    't'
    |'lw'
    |'r2'
    |'r3'
    |'tw'
    |'gs'
    |'nf'
    |'bei'
    |'tv'
    |'tb'
    |TIEFE
    ;

TIEFE: ([0-9]|'.')+;
TEIL: ANY+;
UNBEKANNT: ANY+;
ANY: [a-z]|[A-Z]|[0-9]|'^'|'*'|'+';
FRAGLICH: '?';
SICHER: '!';
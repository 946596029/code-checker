parser grammar MdParser;

options { tokenVocab=MdLexer; }

// document := blocks separated by newlines and/or blank tokens
document
  : (block (NEWLINE)+)* block? EOF
  ;

block
  : headerBlock
  | listItem
  | blankBlock
  | paragraph
  ;

// <header:n> Title text
headerBlock
  : HEADER inlineText?
  ;

// list items support optional leading INDENT/TAB tokens
// e.g. <INDENT:2>- item text
listItem
  : (INDENT | TAB)* (DASH | PLUS | STAR) inlineText?
  ;

// one or more blank markers possibly mixed with NEWLINEs
blankBlock
  : (BLANK | NEWLINE)+
  ;

// paragraph = a line without structural markers, i.e., pure TEXT (and optional SPACE_TOK)
paragraph
  : (SPACE_TOK)? TEXT
  ;

// inline text = optional SPACE_TOK then TEXT (consume the rest of the line as text)
inlineText
  : (SPACE_TOK)? TEXT
  ;



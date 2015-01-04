// Hibernateの設定
//   StringからPostgreSQLの型へのマッピングのデフォルトを変更。VARCHARではなくTEXTにマッピングされるようにする。
//      参照
//        http://docs.jboss.org/hibernate/orm/4.3/manual/en-US/html_single/#mapping-declaration-property
//        5.1.4.1.1. Type
@TypeDef(defaultForType = String.class, typeClass = org.hibernate.type.TextType.class)
package hibernatestudy;

import org.hibernate.annotations.TypeDef;

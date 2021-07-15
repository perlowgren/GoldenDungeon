package net.spirangle.sphinx;

import java.util.HashMap;

public class StringUtils {
	public static final int ESCAPE_QUOTE    = 0x00000001;   // Escape/Unescape quotes.
	public static final int ESCAPE_SL_EOL   = 0x00000002;   // Escape '\n' so that it becomes '\\' and '\n' instead of "\\n".
	public static final int ESCAPE_HEX      = 0x00000004;   // Escape/Unescape hexadecimal characters, so that '~' becomes "\\x7F".
	public static final int ESCAPE_UNICODE  = 0x00000008;   // Escape/Unescape unicode characters.
	public static final int ESCAPE          = 0x00000009;   // Default for escape(). Escape quotes, breaks, tabs and unicode.
	public static final int UNESCAPE        = 0x0000000f;   // Default for unescape(). Unescape all escape sequences.

	/*public static final int HTML_QUOTE      = 0x00000001;   //
	public static final int HTML_AMP        = 0x00000002;   //
	public static final int HTML_LTGT       = 0x00000004;   //
	public static final int HTML_NAMED      = 0x000000ff;   //
	public static final int HTML_CODES      = 0x00000100;   //
	public static final int HTML_UNICODE    = 0x00000200;   //
	public static final int HTML_ALL        = 0xffffffff;   //

	private static final String[] htmlEntities = {
		"#0",         // 0
		"#1",         // 1
		"#2",         // 2
		"#3",         // 3
		"#4",         // 4
		"#5",         // 5
		"#6",         // 6
		"#7",         // 7
		"#8",         // 8
		null,         // 9     <TAB>
		null,         // 10    <NEW_LINE>
		null,         // 11    <VTAB>
		null,         // 12    <FORM_FEED>
		null,         // 13    <CARRIAGE_RETURN>
		"#14",        // 14
		"#15",        // 15
		"#16",        // 16
		"#17",        // 17
		"#18",        // 18
		"#19",        // 19
		"#20",        // 20
		"#21",        // 21
		"#22",        // 22
		"#23",        // 23
		"#24",        // 24
		"#25",        // 25
		"#26",        // 26
		"#27",        // 27
		"#28",        // 28
		"#29",        // 29
		"#30",        // 30
		"#31",        // 31
		null,         // 32    <SPACE>
		null,         // 33    !
		"quot",       // 34    "    &quot;
		null,         // 35    #
		null,         // 36    $
		null,         // 37    %
		"amp",        // 38    &    &amp;
		"apos",       // 39    '    &apos;
		null,         // 40    (
		null,         // 41    )
		null,         // 42    *
		null,         // 43    +
		null,         // 44    ,
		null,         // 45    -
		null,         // 46    .
		null,         // 47    /
		null,         // 48    0
		null,         // 49    1
		null,         // 50    2
		null,         // 51    3
		null,         // 52    4
		null,         // 53    5
		null,         // 54    6
		null,         // 55    7
		null,         // 56    8
		null,         // 57    9
		null,         // 58    :
		null,         // 59    ;
		"lt",         // 60    <    &lt;
		null,         // 61    =
		"gt",         // 62    >    &gt;
		null,         // 63    ?
		null,         // 64    @
		null,         // 65    A
		null,         // 66    B
		null,         // 67    C
		null,         // 68    D
		null,         // 69    E
		null,         // 70    F
		null,         // 71    G
		null,         // 72    H
		null,         // 73    I
		null,         // 74    J
		null,         // 75    K
		null,         // 76    L
		null,         // 77    M
		null,         // 78    N
		null,         // 79    O
		null,         // 80    P
		null,         // 81    Q
		null,         // 82    R
		null,         // 83    S
		null,         // 84    T
		null,         // 85    U
		null,         // 86    V
		null,         // 87    W
		null,         // 88    X
		null,         // 89    Y
		null,         // 90    Z
		null,         // 91    [
		null,         // 92    '\'
		null,         // 93    ]
		null,         // 94    ^
		null,         // 95    _
		null,         // 96    `
		null,         // 97    a
		null,         // 98    b
		null,         // 99    c
		null,         // 100   d
		null,         // 101   e
		null,         // 102   f
		null,         // 103   g
		null,         // 104   h
		null,         // 105   i
		null,         // 106   j
		null,         // 107   k
		null,         // 108   l
		null,         // 109   m
		null,         // 110   n
		null,         // 111   o
		null,         // 112   p
		null,         // 113   q
		null,         // 114   r
		null,         // 115   s
		null,         // 116   t
		null,         // 117   u
		null,         // 118   v
		null,         // 119   w
		null,         // 120   x
		null,         // 121   y
		null,         // 122   z
		null,         // 123   new HtmlEntity(
		null,         // 124   |
		null,         // 125   )
		null,         // 126   ~
		"#127",       // 127
		"#128",       // 128
		"#129",       // 129
		"#130",       // 130
		"#131",       // 131
		"#132",       // 132
		"#133",       // 133
		"#134",       // 134
		"#135",       // 135
		"#136",       // 136
		"#137",       // 137
		"#138",       // 138
		"#139",       // 139
		"#140",       // 140
		"#141",       // 141
		"#142",       // 142
		"#143",       // 143
		"#144",       // 144
		"#145",       // 145
		"#146",       // 146
		"#147",       // 147
		"#148",       // 148
		"#149",       // 149
		"#150",       // 150
		"#151",       // 151
		"#152",       // 152
		"#153",       // 153
		"#154",       // 154
		"#155",       // 155
		"#156",       // 156
		"#157",       // 157
		"#158",       // 158
		"#159",       // 159
		"nbsp",       // 160        &nbsp;
		"iexcl",      // 161   ¡    inverted exclamation mark     &iexcl;   &#161;
		"cent",       // 162   ¢    cent                          &cent;    &#162;
		"pound",      // 163   £    pound                         &pound;   &#163;
		"curren",     // 164   ¤    currency                      &curren;  &#164;
		"yen",        // 165   ¥    yen                           &yen;     &#165;
		"brvbar",     // 166   ¦    broken vertical bar           &brvbar;  &#166;
		"sect",       // 167   §    section                       &sect;    &#167;
		"uml",        // 168   ¨    spacing diaeresis             &uml;     &#168;
		"copy",       // 169   ©    copyright                     &copy;    &#169;
		"ordf",       // 170   ª    feminine ordinal indicator    &ordf;    &#170;
		"laquo",      // 171   «    angle quotation mark (left)   &laquo;   &#171;
		"not",        // 172   ¬    negation                      &not;     &#172;
		"shy",        // 173   ­     soft hyphen                   &shy;     &#173;
		"reg",        // 174   ®    registered trademark          &reg;     &#174;
		"macr",       // 175   ¯    spacing macron                &macr;    &#175;
		"deg",        // 176   °    degree                        &deg;     &#176;
		"plusmn",     // 177   ±    plus-or-minus                 &plusmn;  &#177;
		"sup2",       // 178   ²    superscript 2                 &sup2;    &#178;
		"sup3",       // 179   ³    superscript 3                 &sup3;    &#179;
		"acute",      // 180   ´    spacing acute                 &acute;   &#180;
		"micro",      // 181   µ    micro                         &micro;   &#181;
		"para",       // 182   ¶    paragraph                     &para;    &#182;
		"middot",     // 183   ·    middle dot                    &middot;  &#183;
		"cedil",      // 184   ¸    spacing cedilla               &cedil;   &#184;
		"sup1",       // 185   ¹    superscript 1                 &sup1;    &#185;
		"ordm",       // 186   º    masculine ordinal indicator   &ordm;    &#186;
		"raquo",      // 187   »    angle quotation mark (right)  &raquo;   &#187;
		"frac14",     // 188   ¼    fraction 1/4                  &frac14;  &#188;
		"frac12",     // 189   ½    fraction 1/2                  &frac12;  &#189;
		"frac34",     // 190   ¾    fraction 3/4                  &frac34;  &#190;
		"iquest",     // 191   ¿    inverted question mark        &iquest;  &#191;
		"Agrave",     // 192   À    capital a, grave accent       &Agrave;  &#192;
		"Aacute",     // 193   Á    capital a, acute accent       &Aacute;  &#193;
		"Acirc",      // 194   Â    capital a, circumflex accent  &Acirc;   &#194;
		"Atilde",     // 195   Ã    capital a, tilde              &Atilde;  &#195;
		"Auml",       // 196   Ä    capital a, umlaut mark        &Auml;    &#196;
		"Aring",      // 197   Å    capital a, ring               &Aring;   &#197;
		"AElig",      // 198   Æ    capital ae                    &AElig;   &#198;
		"Ccedil",     // 199   Ç    capital c, cedilla            &Ccedil;  &#199;
		"Egrave",     // 200   È    capital e, grave accent       &Egrave;  &#200;
		"Eacute",     // 201   É    capital e, acute accent       &Eacute;  &#201;
		"Ecirc",      // 202   Ê    capital e, circumflex accent  &Ecirc;   &#202;
		"Euml",       // 203   Ë    capital e, umlaut mark        &Euml;    &#203;
		"Igrave",     // 204   Ì    capital i, grave accent       &Igrave;  &#204;
		"Iacute",     // 205   Í    capital i, acute accent       &Iacute;  &#205;
		"Icirc",      // 206   Î    capital i, circumflex accent  &Icirc;   &#206;
		"Iuml",       // 207   Ï    capital i, umlaut mark        &Iuml;    &#207;
		"ETH",        // 208   Ð    capital eth, Icelandic        &ETH;     &#208;
		"Ntilde",     // 209   Ñ    capital n, tilde              &Ntilde;  &#209;
		"Ograve",     // 210   Ò    capital o, grave accent       &Ograve;  &#210;
		"Oacute",     // 211   Ó    capital o, acute accent       &Oacute;  &#211;
		"Ocirc",      // 212   Ô    capital o, circumflex accent  &Ocirc;   &#212;
		"Otilde",     // 213   Õ    capital o, tilde              &Otilde;  &#213;
		"Ouml",       // 214   Ö    capital o, umlaut mark        &Ouml;    &#214;
		"times",      // 215   ×    multiplication                &times;   &#215;
		"Oslash",     // 216   Ø    capital o, slash              &Oslash;  &#216;
		"Ugrave",     // 217   Ù    capital u, grave accent       &Ugrave;  &#217;
		"Uacute",     // 218   Ú    capital u, acute accent       &Uacute;  &#218;
		"Ucirc",      // 219   Û    capital u, circumflex accent  &Ucirc;   &#219;
		"Uuml",       // 220   Ü    capital u, umlaut mark        &Uuml;    &#220;
		"Yacute",     // 221   Ý    capital y, acute accent       &Yacute;  &#221;
		"THORN",      // 222   Þ    capital THORN, Icelandic      &THORN;   &#222;
		"szlig",      // 223   ß    small sharp s, German         &szlig;   &#223;
		"agrave",     // 224   à    small a, grave accent         &agrave;  &#224;
		"aacute",     // 225   á    small a, acute accent         &aacute;  &#225;
		"acirc",      // 226   â    small a, circumflex accent    &acirc;   &#226;
		"atilde",     // 227   ã    small a, tilde                &atilde;  &#227;
		"auml",       // 228   ä    small a, umlaut mark          &auml;    &#228;
		"aring",      // 229   å    small a, ring                 &aring;   &#229;
		"aelig",      // 230   æ    small ae                      &aelig;   &#230;
		"ccedil",     // 231   ç    small c, cedilla              &ccedil;  &#231;
		"egrave",     // 232   è    small e, grave accent         &egrave;  &#232;
		"eacute",     // 233   é    small e, acute accent         &eacute;  &#233;
		"ecirc",      // 234   ê    small e, circumflex accent    &ecirc;   &#234;
		"euml",       // 235   ë    small e, umlaut mark          &euml;    &#235;
		"igrave",     // 236   ì    small i, grave accent         &igrave;  &#236;
		"iacute",     // 237   í    small i, acute accent         &iacute;  &#237;
		"icirc",      // 238   î    small i, circumflex accent    &icirc;   &#238;
		"iuml",       // 239   ï    small i, umlaut mark          &iuml;    &#239;
		"eth",        // 240   ð    small eth, Icelandic          &eth;     &#240;
		"ntilde",     // 241   ñ    small n, tilde                &ntilde;  &#241;
		"ograve",     // 242   ò    small o, grave accent         &ograve;  &#242;
		"oacute",     // 243   ó    small o, acute accent         &oacute;  &#243;
		"ocirc",      // 244   ô    small o, circumflex accent    &ocirc;   &#244;
		"otilde",     // 245   õ    small o, tilde                &otilde;  &#245;
		"ouml",       // 246   ö    small o, umlaut mark          &ouml;    &#246;
		"divide",     // 247   ÷    division                      &divide;  &#247;
		"oslash",     // 248   ø    small o, slash                &oslash;  &#248;
		"ugrave",     // 249   ù    small u, grave accent         &ugrave;  &#249;
		"uacute",     // 250   ú    small u, acute accent         &uacute;  &#250;
		"ucirc",      // 251   û    small u, circumflex accent    &ucirc;   &#251;
		"uuml",       // 252   ü    small u, umlaut mark          &uuml;    &#252;
		"yacute",     // 253   ý    small y, acute accent         &yacute;  &#253;
		"thorn",      // 254   þ    small thorn, Icelandic        &thorn;   &#254;
		"yuml",       // 255   ÿ    small y, umlaut mark          &yuml;    &#255;
	};

	private static HashMap<String,Integer> htmlEntitiesMap = null;*/

	private static final String escapeSequences  = "'\"?\\\b\f\n\r\t";
	private static final String escapeChars      = "0       btn fr";


	/*public static boolean isHtmlEntity(char c) {
		return c>=0 && c<=255 && htmlEntities[c]!=null;
	}

	private static final String selectEntity(char c,int f) {
		if(c<0 || c>255 ||
		   ((c=='\'' || c=='"') && (f&HTML_QUOTE)==0) ||
		   ((c=='&') && (f&HTML_AMP)==0) ||
		   ((c=='<' || c=='>') && (f&HTML_LTGT)==0)) return null;
		String e = htmlEntities[c];
		if((f&HTML_CODES)==0 && e!=null && e.charAt(0)=='#') return null;
		return e;
	}

	public static final String encodeHtml(String str) {
		return encodeHtml(str,0,0,HTML_ALL);
	}

	public static final String encodeHtml(String str,int o,int l,int f) {
		if(str!=null && str.length()>0) {
			if(o<0) o = str.length()+o;
			if(l<=0) l = str.length()-o+l;
			if(o>=0 && l>0 && o+l<=str.length()) {
				StringBuilder b = null;
				String e;
				int i,p0,p1;
				for(i=0,p0=o,p1=o+l; p0<p1; ++p0) {
					e = selectEntity(str.charAt(p0),f);
					if(e!=null) {
						if(b==null) b = new StringBuilder(str.length());
						if(p0>i) b.append(str,i,p0);
						b.append('&').append(e).append(';');
						i = p0+1;
					}
				}
				if(b!=null) {
					if(i<str.length())
						b.append(str,i,str.length());
					return b.toString();
				}
			}
		}
		return str;
	}

	public static final String decodeHtml(String str) {
		return decodeHtml(str,0,0);
	}

	public static final String decodeHtml(String str,int o,int l) {
		if(htmlEntitiesMap==null) {
			htmlEntitiesMap = new HashMap<String,Integer>();
			for(int i=0; i<htmlEntities.length; ++i)
				if(htmlEntities[i]!=null)
					htmlEntitiesMap.put(htmlEntities[i],i);
		}
		if(str!=null && str.length()>0) {
			if(o<0) o = str.length()+o;
			if(l<=0) l = str.length()-o+l;
			if(o>=0 && l>0 && o+l<=str.length()) {
				StringBuilder b = null;
				String e;
				int i,n,r,p0,p1,p2,p3;
				for(i=0,p0=o,p3=o+l; p0<p3; ++p0) {
					if(str.charAt(p0)=='&') {
						for(p1=p0+1,p2=p1; p2<p1+6 && str.charAt(p2)!=';'; ++p2);
						if(p2<p1+2 || p2>=p1+6) continue;
						if(str.charAt(p1)=='#') {
							++p1;
							r = 10;
							if(str.charAt(p1)=='x' || str.charAt(p1)=='X') { r = 16;++p1; }
							n = Integer.parseInt(str.substring(p1,p2),r);
						} else {
							e = str.substring(p1,p2);
							n = htmlEntitiesMap.get(e);
						}
						if(b==null) b = new StringBuilder(str.length());
						if(p1>i) b.append(str,i,p0);
						b.append((char)n);
						i = p2+1;
						p0 = p2;
					}
				}
				if(b!=null) {
					if(i<str.length())
						b.append(str,i,str.length());
					return b.toString();
				}
			}
		}
		return str;
	}*/

	public static final String escape(String str) {
		return escape(str,0,0,ESCAPE);
	}

	public static final String escape(String str,int o,int l,int f) {
		if(str!=null && str.length()>0) {
			if(o<0) o = str.length()+o;
			if(l<=0) l = str.length()-o+l;
			if(o>=0 && l>0 && o+l<=str.length()) {
				StringBuilder b = null;
				int i,n,p0,p1;
				for(i=0,p0=o,p1=o+l; p0<p1; ++p0) {
					if(escapeSequences.indexOf(n=(int)str.charAt(p0))!=-1 &&
							((n!='\'' && n!='"') || (f&ESCAPE_QUOTE)!=0)) {
						n = n<=0x0d && (n!='\n' || (f&ESCAPE_SL_EOL)==0)? escapeChars.charAt(n) : n;
						if(b==null) b = new StringBuilder(str.length());
						if(p1>i) b.append(str,i,p0);
						b.append('\\').append((char)n);
						i = p0+1;
					}
				}
				if(b!=null) {
					if(i<str.length())
						b.append(str,i,str.length());
					return b.toString();
				}
			}
		}
		return str;
	}

	public static final String unescape(String str) {
		return unescape(str,0,0,UNESCAPE);
	}

	public static final String unescape(String str,int o,int l,int f) {
		if(str!=null && str.length()>0) {
			if(o<0) o = str.length()+o;
			if(l<=0) l = str.length()-o+l;
			if(o>=0 && l>0 && o+l<=str.length()) {
				StringBuilder b = null;
				int i,n,c,p0,p1;
				for(i=0,p0=o,p1=o+l; p0<p1; ++p0) {
					if(str.charAt(p0)=='\\') {
						n = -1;
						if(((c=(int)str.charAt(p0+1))!=' ' && (n=escapeChars.indexOf(c))!=-1) ||
							(c!='\'' && c!='"') || (f&ESCAPE_QUOTE)!=0) {
							if(n!=-1) c = n;
							if(b==null) b = new StringBuilder(str.length());
							if(p0>i) b.append(str,i,p0);
							b.append((char)c);
							i = p0+2;
							++p0;
						}
					}
				}
				if(b!=null) {
					if(i<str.length())
						b.append(str,i,str.length());
					return b.toString();
				}
			}
		}
		return str;
	}
}


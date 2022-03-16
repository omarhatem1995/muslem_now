package com.myapplication

class SuraNameUtil {
    companion object{
        fun getSuraNameByUnicodeFromSuraNumber(suraNumber:Int):String{
            return when(suraNumber){
                1 -> "\u005D"
                2 -> "\u005E"
                3 -> "\u005F"
                4 -> "\u0060"
                5 -> "\u0061"
                6 -> "\u0062"
                7 -> "\u0063"
                8 -> "\u0064"
                9 -> "\u0065"
                10 -> "\u0066"
                11 -> "\u0067"
                12 -> "\u0068"
                13 -> "\u0069"
                14 -> "\u006A"
                15 -> "\u006B"
                16 -> "\u006C"
                17 -> "\u006D"
                18 -> "\u006E"
                19 -> "\u006F"
                20 -> "\u0070"
                21 -> "\u0071"
                22 -> "\u0072"
                23 -> "\u0073"
                24 -> "\u0074"
                25 -> "\u0075"
                26 -> "\u0076"
                27 -> "\u0077"
                28 -> "\u0078"
                29 -> "\u0079"
                30 -> "\u007A"
                31 -> "\u007B"
                32 -> "\u007C"
                33 -> "\u007D"
                34 -> "\u007E"
                35 -> "\u007F"
                36 -> "\u00A1"
                37 -> "\u00A2"
                38 -> "\u00A3"
                39 -> "\u00A4"
                40 -> "\u00A5"
                41 -> "\u00A6"
                42 -> "\u00A7"
                43 -> "\u00A8"
                44 -> "\u00A9"
                45 -> "\u00AA"
                46 -> "\u00AB"
                47 -> "\u00AC"
                48 -> "\u00AE"
                49 -> "\u00AF"
                50 -> "\u00B0"
                51 -> "\u00B1"
                52 -> "\u00B2"
                53 -> "\u00B3"
                54 -> "\u00B4"
                55 -> "\u00B5"
                56 -> "\u00B6"
                57 -> "\u00B8"
                58 -> "\u00B9"
                59 -> "\u00BA"
                60 -> "\u00BB"
                61 -> "\u00BC"
                62 -> "\u00BD"
                63 -> "\u00BE"
                64 -> "\u00BF"
                65 -> "\u00C0"
                66 -> "\u00C1"
                67 -> "\u00C2"
                68 -> "\u00C3"
                69 -> "\u00C4"
                70 -> "\u00C5"
                71 -> "\u00C6"
                72 -> "\u00C7"
                73 -> "\u00C8"
                74 -> "\u00C9"
                75 -> "\u00CA"
                76 -> "\u00CB"
                77 -> "\u00CC"
                78 -> "\u00CD"
                79 -> "\u00CE"
                80 -> "\u00CF"
                81 -> "\u00D0"
                82 -> "\u00D1"
                83 -> "\u00D2"
                84 -> "\u00D3"
                85 -> "\u00D4"
                86 -> "\u00D5"
                87 -> "\u00D6"
                88 -> "\u00D7"
                89 -> "\u00D8"
                90 -> "\u00D9"
                91 -> "\u00DA"
                92 -> "\u00DB"
                93 -> "\u00DC"
                94 -> "\u00DD"
                95 -> "\u00DE"
                96 -> "\u00DF"
                97 -> "\u00E0"
                98 -> "\u00E1"
                99 -> "\u00E2"
                100 -> "\u00E3"
                101 -> "\u00E4"
                102 -> "\u00E5"
                103 -> "\u00E6"
                104 -> "\u00E7"
                105 -> "\u00E8"
                106 -> "\u00E9"
                107 -> "\u00EA"
                108 -> "\u00EB"
                109 -> "\u00EC"
                110 -> "\u00ED"
                111 -> "\u00EE"
                112 -> "\u00EF"
                113 -> "\u00F0"
                114 -> "\u00F1"
                else -> ""
            }
        }
    }
}
package io.github.jae02546.ekinarabe

//2022-05-29 19:29:31 駅並べ路線データジェネレータ ver.2.0.0.11 (c) 2022 jae02546@gmail.com
object DatasetSelLine {
    //路線選択データセット
    //MutableMap<フィルタNo, MutableList<路線No>>
    val selLineMap: MutableMap <Int, MutableList<Int>> = mutableMapOf(
        0 to mutableListOf(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 1010, 1020, 1030, 1040, 1050, 1060, 1070, 1080, 1090, 1100, 1110, 1120, 1130, 1140, 1150, 1160, 1170, 1180, 1190, 1200, 1210, 1220, 1230, 3010, 3020, 3030, 3040, 3050, 3060, 3070, 3080, 3090, 3100, 3110, 3120, 3130, 3140, 3150, 3160, 3170, 3180, 3190, 3200, 3210, 3220, 3230, 3240, 3250, 3260, 3270, 3280, 3290, 3300, 3310, 3320, 3330, 3340, 3350, 3360, 3370, 3380, 3390, 3400, 3410, 3420, 3430, 3440, 3450, 3460, 3470, 3480, 3490, 3500, 3510, 3520, 3530, 3540, 3550, 3560, 3570, 3580, 3590, 3600, 3610, 3620, 3630, 3640, 3650, 3660, 3670, 3680, 3690, 3700, 3710, 3720, 3730, 3740, 3750, 3760, 3770, 3780, 3790, 3800, 3810, 3820, 3830, 3840, 3850, 3860, 3870, 3880, 3890, 3900, 3910, 3920, 4010, 4020, 4030, 4040, 4050, 4060, 4070, 4080, 4090, 4100, 4110, 4120, 4130, 4140, 4150, 4160, 4170, 5010, 5020, 5030, 5040, 5050, 5060, 5070, 5080, 5090, 5100, 5110, 5120, 5130, 5140, 5150, 5160, 5170, 5180, 5190, 5200, 5210, 5220, 5230, 5240, 5250, 5260, 5270, 5280, 5290, 5300, 5310, 5320, 5330, 5340, 5350, 5360, 5370, 5380, 5390, 5400, 5410, 5420, 5430, 5440, 5450, 5460, 5470, 5480, 5490, 5500, 5510, 5520, 5530, 5540, 5550, 5560, 5570, 5580, 5590, 5600, 5610, 5620, 5630, 5640, 5650, 5660, 5670, 7010, 7020, 7030, 7040, 7050, 7060, 7070, 7080, 7090, 7100, 8010, 8020, 8030, 8040, 8050, 8060, 8070, 8080, 8090, 8100, 8110, 8120, 8130, 8140, 8150, 8160, 8170, 8180, 8190, 8200, 8210, 8220, 8230, 8240, 8250, 8260, 8270, 8280, 8290, 8300, 8310, 30010, 30020, 30030, 30040, 30050, 30060, 30070, 30080, 30090, 30100, 30110, 30120, 30130, 30140, 30150, 31010, 31020, 31030, 31040, 31050, 31060, 31070, 31080, 31090, 31100, 31110, 31120, 32010, 32020, 32030, 32040, 32050, 32060, 32070, 33010, 33020, 33030, 33040, 33050, 33060, 33070, 34010, 34020, 34030, 34040, 34050, 34060, 34070, 34080, 35010, 35020, 35030, 35040, 35050, 36010, 36020, 36030, 36040, 36050, 36060, 36070, 36080, 36090, 36100, 37010, 37020, 37030, 37040, 38010, 38020, 38030, 40010, 40020, 40030, 40040, 40050, 40060, 40070, 40080, 40090, 40100, 40110, 40120, 40130, 40140, 40150, 40160, 40170, 40180, 40190, 40200, 40210, 40220, 40230, 40240, 40250, 40260, 40270, 40280, 50010, 50020, 50030, 50040, 50050, 50060, 50070, 50080, 50090, 50100, 50110, 50120, 50130, 50140, 50150, 50160, 50170, 50180, 50190, 50200, 50210, 51010, 51020, 51030, 51040, 51050, 51060, 51070, 51080, 52010, 52020, 52030, 52040, 52050, 52060, 52070, 53010, 53020, 53030, 54010, 54020, 54030, 54040, 54050, 54060, 54070, 54080, 54090, 80010, 80020, 80030, 80040, 80050, 100010, 100020, 100030, 100040, 101010, 101020, 102010, 103010, 200010, 201010, 201020, 202010, 203010, 204010, 205010, 206010, 207010, 208010, 209010, 210010, 211010, 211020, 212010, 213010, 214010, 215010, 216010, 300010, 300020, 300030, 300040, 300050, 300060, 300070, 301010, 302010, 303010, 304010, 305010, 306010, 307010, 307020, 308010, 309010, 310010, 311010, 312010, 422020, 313010, 314010, 315010, 316010, 317010, 318010, 318020, 319010, 320010, 321010, 322010, 323010, 324010, 325010, 326010, 327010, 328010, 329010, 330010, 331010, 332010, 332020, 333010, 334010, 335010, 336010, 400010, 401010, 402010, 402020, 403010, 404010, 404020, 404030, 405010, 406010, 406020, 407010, 408010, 409010, 409020, 409030, 409040, 409050, 409060, 409070, 409080, 409090, 410010, 411010, 412010, 413010, 413020, 414010, 414020, 415010, 415020, 416010, 417010, 417020, 418010, 419010, 420010, 421010, 422010, 423010, 424010, 425010, 425020, 426010, 427010, 428010, 428020, 428030, 429010, 430010, 431010, 432010, 433010, 433020, 433030, 433040, 433050, 433060, 434010, 435010, 436010, 436020, 437010, 437020, 438010, 439010, 500010, 500020, 500030, 501010, 502010, 502020, 502030, 503010, 503020, 504010, 504020, 505010, 505020, 506010, 507010, 507020, 508010, 509010, 509020, 509030, 509040, 509050, 509060, 509070, 509080, 509090, 510010, 510020, 511010, 512010, 513010, 514010, 515010, 516010, 516020, 517010, 517020, 517030, 518010, 518020, 518030, 519010, 519020, 519030, 520010, 520020, 520030, 520040, 521010, 522010, 523010, 523020, 524010, 600010, 601010, 601020, 602010, 602020, 603010, 604010, 605010, 606010, 607010, 607020, 607030, 607040, 607050, 607060, 607070, 607080, 608010, 700010, 700020, 700030, 701010, 702010, 702020, 702030, 702040, 702050, 702060, 702070, 702080, 703010, 703020, 703030, 704010, 704020, 704030, 800010, 801010, 802010, 800020, 800030, 800040, 803010, 803020, 803030, 804010, 805010, 805020, 806010, 807010, 807020, 807030, 807040, 807050, 808010, 808020, 809010, 809020, 810010, 811010, 812010, 813010, 814010, 814020, 814030, 815010),
        1 to mutableListOf(100, 1010, 1020, 1030, 1040, 1050, 1060, 1070, 1080, 1090, 1100, 1110, 1120, 1130, 1140, 1150, 1160, 1170, 1180, 1190, 1200, 1210, 1220, 1230, 100010, 100020, 100030, 100040, 101010, 101020, 102010, 103010),
        2 to mutableListOf(30, 60, 70, 100, 3920, 3910, 3900, 3890, 3880, 3870, 3860, 3850, 3840, 3830, 3820, 3810, 3800, 3790, 3780, 3770, 3760, 3750, 3740, 3730, 3720, 3700, 3710, 3690, 3660, 3670, 3680, 3650, 3540, 3080, 3090, 3030, 3040, 3050, 200010, 201010, 201020, 202010, 203010, 204010, 205010, 206010, 207010, 208010, 209010, 210010, 211010, 211020, 212010, 213010, 214010, 215010, 216010),
        3 to mutableListOf(30, 40, 50, 10, 3010, 3020, 3030, 3060, 3070, 3080, 3100, 3110, 3120, 3130, 3150, 3160, 3170, 3180, 3190, 3200, 3210, 3220, 3230, 3240, 3250, 3260, 3270, 3280, 3290, 3300, 3310, 3320, 3330, 3340, 3350, 3360, 3370, 4050, 3380, 3390, 3400, 3410, 3420, 3430, 3440, 3450, 3460, 3470, 3480, 3490, 3500, 3510, 3520, 3530, 3540, 3550, 30010, 30020, 30030, 30040, 30050, 30060, 30070, 30080, 30090, 30100, 30110, 30120, 30130, 30140, 30150, 31010, 31020, 31030, 31040, 31050, 31060, 31070, 31080, 31090, 31100, 31110, 31120, 32010, 32020, 32030, 32040, 32050, 32060, 32070, 33010, 33020, 33030, 33040, 33050, 33060, 33070, 34010, 34020, 34030, 34040, 34050, 34060, 34070, 34080, 35010, 35020, 35030, 35040, 35050, 36010, 36020, 36030, 36040, 36050, 36060, 36070, 36080, 36090, 36100, 37010, 37020, 37030, 37040, 38010, 38020, 38030, 300010, 300020, 300030, 300040, 300050, 300060, 300070, 301010, 302010, 303010, 304010, 305010, 306010, 307010, 307020, 308010, 309010, 310010, 311010, 312010, 422020, 313010, 314010, 315010, 316010, 317010, 318010, 318020, 319010, 320010, 321010, 322010, 323010, 324010, 325010, 326010, 327010, 328010, 329010, 330010, 331010, 332010, 332020, 333010, 334010, 335010, 336010),
        4 to mutableListOf(10, 40, 50, 80, 3130, 3140, 3480, 3560, 3570, 5010, 3580, 3590, 3600, 3610, 3620, 3630, 3640, 3650, 3710, 3720, 3770, 3010, 4010, 4020, 4030, 4040, 4050, 4060, 4070, 4080, 4090, 4100, 4110, 4120, 4130, 5020, 5030, 5040, 5050, 5060, 5070, 5090, 4140, 5200, 4150, 4160, 4170, 40010, 40020, 40030, 40040, 40050, 40060, 40070, 40080, 40090, 40100, 40110, 40120, 40130, 40140, 40150, 40160, 40170, 40180, 40190, 40200, 40210, 40220, 40230, 40240, 40250, 40260, 40270, 40280, 50180, 50190, 50200, 50150, 50160, 50170, 50040, 400010, 401010, 402010, 402020, 403010, 404010, 404020, 404030, 405010, 406010, 406020, 407010, 408010, 409010, 409020, 409030, 409040, 409050, 409060, 409070, 409080, 409090, 410010, 411010, 412010, 413010, 413020, 414010, 414020, 415010, 415020, 416010, 417010, 417020, 418010, 419010, 420010, 421010, 422010, 423010, 424010, 425010, 425020, 426010, 427010, 428010, 428020, 428030, 429010, 430010, 431010, 432010, 433010, 433020, 433030, 433040, 433050, 433060, 434010, 435010, 436010, 436020, 437010, 437020, 438010, 439010),
        5 to mutableListOf(10, 20, 4020, 5110, 5120, 5130, 5070, 5080, 5090, 5100, 5140, 5150, 5160, 5200, 5210, 5220, 5230, 5240, 5250, 5260, 5270, 5280, 5290, 5300, 5310, 5320, 5330, 5340, 5350, 4200, 5360, 5370, 5380, 5390, 5430, 5440, 5450, 5460, 5470, 50010, 50020, 50030, 50040, 50050, 50060, 50070, 50080, 50090, 50100, 50110, 50120, 50130, 50140, 50210, 51010, 51020, 51030, 51040, 51050, 51060, 51070, 51080, 52010, 52020, 52030, 52040, 52050, 52060, 52070, 53010, 53020, 53030, 54010, 54020, 54030, 54040, 54050, 54060, 54070, 54080, 54090, 500010, 500020, 500030, 501010, 502010, 502020, 502030, 503010, 503020, 504010, 504020, 505010, 505020, 506010, 507010, 507020, 508010, 509010, 509020, 509030, 509040, 509050, 509060, 509070, 509080, 509090, 510010, 510020, 511010, 512010, 513010, 514010, 515010, 516010, 516020, 517010, 517020, 517030, 518010, 518020, 518030, 519010, 519020, 519030, 520010, 520020, 520030, 520040, 521010, 522010, 523010, 523020, 524010),
        6 to mutableListOf(20, 5160, 5170, 5180, 5190, 5390, 5400, 5410, 5420, 5460, 5470, 5480, 5490, 5500, 5510, 5520, 5530, 5540, 5550, 5560, 5570, 5580, 5590, 5600, 5610, 5620, 5630, 5640, 5650, 5660, 5670, 524010, 600010, 601010, 601020, 602010, 602020, 603010, 604010, 605010, 606010, 607010, 607020, 607030, 607040, 607050, 607060, 607070, 607080, 608010),
        7 to mutableListOf(5520, 7010, 7020, 7030, 7040, 7050, 7060, 7070, 7080, 7090, 7100, 700010, 700020, 700030, 701010, 702010, 702020, 702030, 702040, 702050, 702060, 702070, 702080, 703010, 703020, 703030, 704010, 704020, 704030),
        8 to mutableListOf(20, 90, 8010, 8020, 8030, 8040, 8050, 8060, 8070, 8080, 8090, 8100, 8110, 8120, 8130, 8140, 8150, 8160, 8170, 8180, 8190, 8200, 8210, 8220, 8230, 8240, 8250, 8260, 8270, 8280, 8290, 8300, 8310, 80010, 80020, 80030, 80040, 80050, 800010, 801010, 802010, 800020, 800030, 800040, 803010, 803020, 803030, 804010, 805010, 805020, 806010, 807010, 807020, 807030, 807040, 807050, 808010, 808020, 809010, 809020, 810010, 811010, 812010, 813010, 814010, 814020, 814030, 815010),
    )
}
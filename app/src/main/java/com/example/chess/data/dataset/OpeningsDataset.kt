package com.example.chess.data.dataset

data class OpeningVariation(
    val id: String,
    val name: String,
    val eco: String,
    val movesSan: List<String>,
    val movesUci: List<String>,
    val description: String,
    val plansWhite: String,
    val plansBlack: String,
    val initialFen: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
)

object OpeningsDataset {
    val openings: List<OpeningVariation> = listOf(
        OpeningVariation(
            id = "op_ruy_lopez",
            name = "Apertura Española (Ruy López)",
            eco = "C60",
            movesSan = listOf("e4", "e5", "Cf3", "Cc6", "Ab5", "a6", "Aa4", "Cf6", "O-O", "Ae7"),
            movesUci = listOf("e2e4", "e7e5", "g1f3", "b8c6", "f1b5", "a7a6", "b5a4", "g8f6", "e1g1", "f8e7"),
            description = "Una de las aperturas más veneradas y profundas del ajedrez. Las blancas presionan al defensor del peón e5 para luchar por el centro a largo plazo.",
            plansWhite = "Controlar el centro con c3 y d4, mantener la tensión y maniobrar el caballo a través de bd2-f1-g3 hacia el flanco de rey.",
            plansBlack = "Defender e5 sólidamente, jugar b5 para ganar espacio en el flanco de dama y preparar el contrajuego central con d6 y d5."
        ),
        OpeningVariation(
            id = "op_sicilian_najdorf",
            name = "Defensa Siciliana (Variante Najdorf)",
            eco = "B90",
            movesSan = listOf("e4", "c5", "Cf3", "d6", "d4", "cxd4", "Cxd4", "Cf6", "Cc3", "a6"),
            movesUci = listOf("e2e4", "c7c5", "g1f3", "d7d6", "d2d4", "c5d4", "f3d4", "g8f6", "b1c3", "a7a6"),
            description = "La respuesta más combativa contra 1.e4. Favorita de Bobby Fischer y Garry Kasparov, combate el centro blanco desde el flanco.",
            plansWhite = "Lanzar un ataque dinámico (Be3, f3, g4 - Ataque Inglés o Bg5), buscar la ruptura en d5 o f5.",
            plansBlack = "Controlar la casilla b5 con a6, preparar contragolpe con e5 o e6, y activar la torre por la columna c semiabierta."
        ),
        OpeningVariation(
            id = "op_queens_gambit",
            name = "Gambito de Dama Declinado",
            eco = "D30",
            movesSan = listOf("d4", "d5", "c4", "e6", "Cc3", "Cf6", "Ag5", "Ae7", "e3", "O-O"),
            movesUci = listOf("d2d4", "d7d5", "c2c4", "e7e6", "b1c3", "g8f6", "c1g5", "f8e7", "e2e3", "e8g8"),
            description = "El estándar de oro posicional contra 1.d4. Las negras consolidan un bastión central inamovible en d5.",
            plansWhite = "Presionar el peón central con cxd5 o mantener la clavada sobre f6; ganar ventaja de espacio en el centro.",
            plansBlack = "Desclavar piezas, preparar la ruptura liberadora con c5 o e5 y activar el alfil de casillas claras."
        ),
        OpeningVariation(
            id = "op_french",
            name = "Defensa Francesa",
            eco = "C00",
            movesSan = listOf("e4", "e6", "d4", "d5", "Cc3", "Cf6", "e5", "Cfd7", "f4", "c5"),
            movesUci = listOf("e2e4", "e7e6", "d2d4", "d7d5", "b1c3", "g8f6", "e4e5", "f6d7", "f2f4", "c7c5"),
            description = "Defensa asimétrica sólida donde las negras crean una cadena de peones y atacan la base blanca en d4.",
            plansWhite = "Afianzar la cadena de peones central e5 y dirigir piezas hacia un ataque sobre el flanco de rey.",
            plansBlack = "Minar la estructura blanca inmediatamente con c5, Cc6 y Db6 presionando fuertemente sobre d4 y b2."
        ),
        OpeningVariation(
            id = "op_caro_kann",
            name = "Defensa Caro-Kann",
            eco = "B10",
            movesSan = listOf("e4", "c6", "d4", "d5", "Cc3", "dxe4", "Cxe4", "Af5", "Cg3", "Ag6"),
            movesUci = listOf("e2e4", "c7c6", "d2d4", "d7d5", "b1c3", "d5e4", "c3e4", "c8f5", "e4g3", "f5g6"),
            description = "Estructura sumamente resistente que permite a las negras desarrollar su alfil de casillas claras fuera de la cadena de peones.",
            plansWhite = "Avanzar peones de flanco con h4-h5 para encerrar el alfil negro y buscar superioridad en el centro.",
            plansBlack = "Completar desarrollo armónico con e6, Cd7, Cgf6 y mantener un final muy superior gracias a la estructura intacta."
        ),
        OpeningVariation(
            id = "op_kings_indian",
            name = "Defensa India de Rey",
            eco = "E60",
            movesSan = listOf("d4", "Cf6", "c4", "g6", "Cc3", "Ag7", "e4", "d6", "Cf3", "O-O"),
            movesUci = listOf("d2d4", "g8f6", "c2c4", "g7g6", "b1c3", "f8g7", "e2e4", "d7d6", "g1f3", "e8g8"),
            description = "Defensa hipermoderna clásica: las negras ceden el centro temporalmente para luego lanzar un contragolpe fulminante con e5 o f5.",
            plansWhite = "Avanzar en el flanco de dama con c5 y b4, abriendo columnas para sus torres.",
            plansBlack = "Cerrar el centro con e5 y lanzar una avalancha de peones en el flanco de rey con f5, g5 y Dh4."
        )
    )
}

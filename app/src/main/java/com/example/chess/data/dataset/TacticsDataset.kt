package com.example.chess.data.dataset

import com.example.chess.model.PieceColor

data class TacticalPuzzle(
    val id: String,
    val title: String,
    val theme: String, // "Tenedores", "Clavadas", "Ataque Descubierto", "Red de Mate", "Sacrificios", "Finales", "Desviación", "Rayos X"
    val difficultyElo: Int,
    val eloTier: String, // "1000-1200", "1200-1400", "1400-1650", "1650-1850", "1850-2100", "2100-2400"
    val initialFen: String,
    val turn: PieceColor,
    val solutionMoves: List<String>, // UCI notation (e.g. "g5f7", "e8e7")
    val solutionSan: List<String>,
    val description: String,
    val hint: String
)

data class EloTier(
    val id: String,
    val name: String,
    val eloRange: String,
    val badgeLabel: String,
    val minElo: Int,
    val maxElo: Int,
    val description: String
)

object TacticsDataset {
    val eloTiers: List<EloTier> = listOf(
        EloTier("tier_1", "Iniciación Táctica", "1000 - 1200", "Principiante", 1000, 1200, "Mates de pasillo y bifurcaciones elementales"),
        EloTier("tier_2", "Jugador de Club", "1200 - 1400", "Intermedio", 1200, 1400, "Clavadas devastadoras y tenedores en centro"),
        EloTier("tier_3", "Táctico Avanzado", "1400 - 1650", "Avanzado", 1400, 1650, "Ataques al descubierto y dobles amenazas"),
        EloTier("tier_4", "Candidato Maestro", "1650 - 1850", "Experto", 1650, 1850, "Sacrificios posicionales y atracciones"),
        EloTier("tier_5", "Maestro Nacional", "1850 - 2100", "Maestro", 1850, 2100, "Redes complejas de mate y cálculo forzado"),
        EloTier("tier_6", "Gran Maestro", "2100 - 2400", "Élite", 2100, 2400, "Finales técnicos y sacrificios de dama")
    )

    val puzzles: List<TacticalPuzzle> = listOf(
        // --- NIVEL 1: 1000 - 1200 (Iniciación y Patrones Fundamentales) ---
        TacticalPuzzle(
            id = "t_01",
            title = "Mate del Pasillo en la Octava",
            theme = "Red de Mate",
            difficultyElo = 1050,
            eloTier = "1000-1200",
            initialFen = "3r2k1/5ppp/8/8/8/8/4QPPP/6K1 w - - 0 1",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("e2e8", "d8e8", "g1f1"),
            solutionSan = listOf("De8+", "Txe8", "Rf1"),
            description = "La primera fila enemiga carece de escape ('respiradero'). Desvía la torre defensora.",
            hint = "Penetra directamente en la octava fila con la dama."
        ),
        TacticalPuzzle(
            id = "t_02",
            title = "Tenedor Elemental de Peón",
            theme = "Tenedores",
            difficultyElo = 1100,
            eloTier = "1000-1200",
            initialFen = "r1bqk2r/pppp1ppp/2n5/2b1p3/4P1n1/2NP1N2/PPP2PPP/R1BQKB1R w KQkq - 3 6",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("d3d4", "e5d4", "f3d4"),
            solutionSan = listOf("d4", "exd4", "Cxd4"),
            description = "Golpea en el centro con el peón d4 para desafiar el alfil y desestabilizar el ataque negro.",
            hint = "Avanza el peón a d4 atacando al alfil en c5."
        ),
        TacticalPuzzle(
            id = "t_03",
            title = "Tenedor de Caballo al Rey y Dama",
            theme = "Tenedores",
            difficultyElo = 1150,
            eloTier = "1000-1200",
            initialFen = "r1b1k2r/pppp1ppp/8/4n3/1b1Nq3/2N5/PPP2PPP/R1BQKB1R w KQkq - 0 8",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("d4e2", "e8g8"),
            solutionSan = listOf("Ce2", "O-O"),
            description = "Las negras han colocado su dama y rey en posición vulnerable. Neutraliza la clavada.",
            hint = "Protege a tu rey bloqueando con el caballo a e2."
        ),
        TacticalPuzzle(
            id = "t_04",
            title = "Bifurcación de Caballo en c7",
            theme = "Tenedores",
            difficultyElo = 1180,
            eloTier = "1000-1200",
            initialFen = "r1bqk2r/pp1p1ppp/2n1pn2/2b5/2PN4/2N1P3/PP3PPP/R1BQKB1R w KQkq - 1 7",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("d4b5", "e8e7", "b5c7"),
            solutionSan = listOf("Cb5", "Re7", "Cxc7"),
            description = "El caballo blanco presiona la débil casilla c7 amenazando bifurcación a rey y torre.",
            hint = "Avanza el caballo a la casilla b5."
        ),

        // --- NIVEL 2: 1200 - 1400 (Jugador de Club) ---
        TacticalPuzzle(
            id = "t_05",
            title = "Clavada Absoluta con Alfil",
            theme = "Clavadas",
            difficultyElo = 1250,
            eloTier = "1200-1400",
            initialFen = "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/3P1N2/PPP2PPP/RNBQK2R w KQkq - 1 5",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("c1g5", "h7h6", "g5f6"),
            solutionSan = listOf("Ag5", "h6", "Axf6"),
            description = "Clava el caballo de f6 a la dama negra, restringiendo la movilidad del rival.",
            hint = "Desarrolla el alfil de casillas oscuras hacia g5."
        ),
        TacticalPuzzle(
            id = "t_06",
            title = "Carrera de Peones y Cuadrado",
            theme = "Finales",
            difficultyElo = 1300,
            eloTier = "1200-1400",
            initialFen = "8/8/4k3/8/4P3/8/8/4K3 w - - 0 1",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("e1e2", "e6e5", "e2e3"),
            solutionSan = listOf("Re2", "Re5", "Re3"),
            description = "Mantén la oposición con el rey blanco para garantizar el avance victorioso del peón.",
            hint = "Acerca tu rey para dar soporte activo al peón pasado."
        ),
        TacticalPuzzle(
            id = "t_07",
            title = "Clavada en la Columna Abierta",
            theme = "Clavadas",
            difficultyElo = 1350,
            eloTier = "1200-1400",
            initialFen = "r1b1k2r/ppp2ppp/2n2q2/3p4/1b1P4/2N2N2/PPP1QPPP/R3KB1R b KQkq - 1 8",
            turn = PieceColor.BLACK,
            solutionMoves = listOf("c8e6", "e1c1", "e8c8"),
            solutionSan = listOf("Ae6", "O-O-O", "O-O-O"),
            description = "El rey negro está en jaque sobre la columna abierta. Interpón una pieza activa sólidamente.",
            hint = "Desarrolla el alfil a e6 para tapar el jaque."
        ),
        TacticalPuzzle(
            id = "t_08",
            title = "Desviación y Ganancia de Pieza",
            theme = "Desviación",
            difficultyElo = 1380,
            eloTier = "1200-1400",
            initialFen = "r1b2rk1/pp3ppp/2n1pn2/2q5/2B5/4PN2/PP1N1PPP/R2Q1RK1 w - - 0 11",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("a1c1", "c5e7", "c4d3"),
            solutionSan = listOf("Tc1", "De7", "Ad3"),
            description = "Aprovecha la columna abierta con la torre y hostiga a la dama negra.",
            hint = "Ubica la torre en la columna c abierta."
        ),

        // --- NIVEL 3: 1400 - 1650 (Táctico Avanzado) ---
        TacticalPuzzle(
            id = "t_09",
            title = "Devastador Ataque Descubierto",
            theme = "Ataque Descubierto",
            difficultyElo = 1420,
            eloTier = "1400-1650",
            initialFen = "r1b2rk1/pp1nbppp/1q2pn2/2p3B1/3P4/2NB1N2/PPP1QPPP/R4RK1 w - - 0 11",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("d3h7", "g8h7", "e2d3"),
            solutionSan = listOf("Axh7+", "Rxh7", "Dd3+"),
            description = "Sacrifica tácticamente en h7 para atraer al rey y ganar tiempo de iniciativa con jaque.",
            hint = "El alfil en d3 apunta directamente al enroque negro."
        ),
        TacticalPuzzle(
            id = "t_10",
            title = "Doble Amenaza Descubierta",
            theme = "Ataque Descubierto",
            difficultyElo = 1480,
            eloTier = "1400-1650",
            initialFen = "r2qk2r/ppp2ppp/3b1n2/4n3/4P1b1/2NB1N2/PPP2PPP/R1BQ1RK1 w kq - 4 8",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("f3e5", "g4d1", "e5f7"),
            solutionSan = listOf("Cxe5", "Axd1", "Cxf7"),
            description = "Comienza con una captura que destapa la presión sobre el flanco de rey y crea amenazas múltiples.",
            hint = "El caballo en f3 puede capturar en e5."
        ),
        TacticalPuzzle(
            id = "t_11",
            title = "Regalo Griego en h7",
            theme = "Sacrificios",
            difficultyElo = 1550,
            eloTier = "1400-1650",
            initialFen = "r1bq1rk1/ppp2ppp/2n1pn2/3p4/1bPP4/2NBPN2/PP3PPP/R1BQK2R w KQ - 4 7",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("d3h7", "g8h7", "f3g5"),
            solutionSan = listOf("Axh7+", "Rxh7", "Cg5+"),
            description = "Destruye la muralla de peones del enroque enemigo con el clásico sacrificio griego.",
            hint = "El alfil en d3 apunta como un misil a la casilla h7."
        ),
        TacticalPuzzle(
            id = "t_12",
            title = "Mate de la Coz Clásico",
            theme = "Red de Mate",
            difficultyElo = 1620,
            eloTier = "1400-1650",
            initialFen = "6k1/5ppp/8/8/8/5N2/5PPP/4Q1K1 w - - 0 1",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("e1e8"),
            solutionSan = listOf("De8#"),
            description = "Aprovecha la falta de casillas de escape de las piezas negras para asestar el golpe definitivo.",
            hint = "Jaque mate directo en la octava fila."
        ),

        // --- NIVEL 4: 1650 - 1850 (Candidato Maestro) ---
        TacticalPuzzle(
            id = "t_13",
            title = "Desviación y Victoria Inmediata",
            theme = "Sacrificios",
            difficultyElo = 1680,
            eloTier = "1650-1850",
            initialFen = "2r3k1/pp3ppp/8/8/8/1Q6/PP3PPP/2q1R1K1 w - - 0 1",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("b3e3", "c1e3", "e1e3"),
            solutionSan = listOf("De3", "Dxe3", "Txe3"),
            description = "Coordina la torre y dama para neutralizar el contraataque rival en la primera fila.",
            hint = "Coloca tu dama en e3 para defender y contraatacar."
        ),
        TacticalPuzzle(
            id = "t_14",
            title = "Atracción al Jaque Doble",
            theme = "Ataque Descubierto",
            difficultyElo = 1740,
            eloTier = "1650-1850",
            initialFen = "r1b2rk1/2q1bppp/p2ppn2/1p2P3/3B4/2NB4/PPP2PPP/R2Q1RK1 w - - 0 13",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("e5f6", "e7f6", "d4f6"),
            solutionSan = listOf("exf6", "Axf6", "Axf6"),
            description = "Elimina la pieza defensora del enroque negro y abre la diagonal letal.",
            hint = "Captura el caballo en f6 con el peón."
        ),
        TacticalPuzzle(
            id = "t_15",
            title = "Ruptura Central en la Siciliana",
            theme = "Tenedores",
            difficultyElo = 1800,
            eloTier = "1650-1850",
            initialFen = "r1bqkb1r/pp2pppp/2np1n2/8/3NP3/2N5/PPP2PPP/R1BQKB1R w KQkq - 2 6",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("d4c6", "b7c6", "e4e5"),
            solutionSan = listOf("Cxc6", "bxc6", "e5"),
            description = "Daña la estructura de peones negra y clava una lanza en e5 creando caos.",
            hint = "Cambia primero en c6 y empuja e5."
        ),

        // --- NIVEL 5: 1850 - 2100 (Maestro Nacional) ---
        TacticalPuzzle(
            id = "t_16",
            title = "Sacrificio Temático de Calidad",
            theme = "Sacrificios",
            difficultyElo = 1890,
            eloTier = "1850-2100",
            initialFen = "r4rk1/1pp1qppp/p1np4/4p3/2B1P1b1/3P1N2/PPP2PPP/R2QR1K1 w - - 0 12",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("c2c3", "e7f6", "e1e3"),
            solutionSan = listOf("c3", "Df6", "Te3"),
            description = "Consolida las casillas clave y maniobra la torre para defender el caballo clavado.",
            hint = "Juega c3 para privar de casillas activas al rival."
        ),
        TacticalPuzzle(
            id = "t_17",
            title = "Red de Mate con Alfiles Cruzados",
            theme = "Red de Mate",
            difficultyElo = 1960,
            eloTier = "1850-2100",
            initialFen = "r2q1rk1/pp3ppp/2n1p3/3p4/3Pn3/2BB1Q1P/PPP2PP1/R4RK1 w - - 1 13",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("d3e4", "d5e4", "f3e4"),
            solutionSan = listOf("Axe4", "dxe4", "Dxe4"),
            description = "Elimina el caballo centralizado y restaura la supremacía de la dama blanca.",
            hint = "Captura con el alfil en e4."
        ),
        TacticalPuzzle(
            id = "t_18",
            title = "Final Técnico de Torres: Puente de Lucena",
            theme = "Finales",
            difficultyElo = 2050,
            eloTier = "1850-2100",
            initialFen = "1K6/1P1k4/8/8/8/8/r7/2R5 w - - 0 1",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("c1c4", "a2a1", "c4d4"),
            solutionSan = listOf("Tc4", "Ta1", "Td4+"),
            description = "Construye el clásico puente de Lucena en la cuarta fila para coronar el peón.",
            hint = "Eleva la torre a la cuarta fila para preparar el escudo."
        ),

        // --- NIVEL 6: 2100 - 2400 (Gran Maestro) ---
        TacticalPuzzle(
            id = "t_19",
            title = "Ataque Inmortal de Anderssen",
            theme = "Sacrificios",
            difficultyElo = 2180,
            eloTier = "2100-2400",
            initialFen = "r1b1k2r/pppp1ppp/8/8/1B1q4/8/PPP2PPP/R3KB1R w KQkq - 0 1",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("b4c3", "d4e4", "f1e2"),
            solutionSan = listOf("Ac3", "De4+", "Ae2"),
            description = "Defensa activa magistral que preserva el alfil clave y acelera el desarrollo del rey.",
            hint = "Retira el alfil a c3 atacando la dama."
        ),
        TacticalPuzzle(
            id = "t_20",
            title = "La Inmortal de Kasparov vs Topalov",
            theme = "Ataque Descubierto",
            difficultyElo = 2290,
            eloTier = "2100-2400",
            initialFen = "r4rk1/pp1b1ppp/1q2pn2/2p3B1/3P4/2NB4/PPP1QPPP/R4RK1 w - - 0 1",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("g5f6", "g7f6", "d4c5"),
            solutionSan = listOf("Axf6", "gxf6", "dxc5"),
            description = "Demuele la estructura defensiva destruyendo el caballo protector de f6.",
            hint = "Captura inmediatamente en f6 con el alfil."
        ),
        TacticalPuzzle(
            id = "t_21",
            title = "Precisión Absoluta en Final de Alfiles Opuestos",
            theme = "Finales",
            difficultyElo = 2380,
            eloTier = "2100-2400",
            initialFen = "8/5p2/4pk2/7p/7P/4B1P1/5PK1/4b3 w - - 0 1",
            turn = PieceColor.WHITE,
            solutionMoves = listOf("g2f3", "f6f5", "e3d4"),
            solutionSan = listOf("Rf3", "Rf5", "Ad4+"),
            description = "Aprovecha la debilidad de las casillas claras para restringir al monarca adversario.",
            hint = "Activa tu rey hacia f3."
        )
    )

    fun getThemes(): List<String> = listOf("Todos", "Tenedores", "Clavadas", "Ataque Descubierto", "Red de Mate", "Sacrificios", "Finales", "Desviación")

    fun getPuzzlesForTier(tierRange: String): List<TacticalPuzzle> =
        puzzles.filter { it.eloTier == tierRange }
}

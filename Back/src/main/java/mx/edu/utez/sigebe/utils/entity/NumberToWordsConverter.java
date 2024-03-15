package mx.edu.utez.sigebe.utils.entity;

public class NumberToWordsConverter {
    private static final String[] UNITS = {
            "", "UN", "DOS", "TRES", "CUATRO",
            "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE"
    };

    private static final String[] TENS = {
            "", "DIEZ", "VEINTE", "TREINTA", "CUARENTA",
            "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA", "NOVENTA"
    };

    private static final String[] SPECIALS = {
            "DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE",
            "QUINCE", "DIECISÉIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE"
    };

    private static final String[] HUNDREDS = {
            "", "CIEN", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS",
            "QUINIENTOS", "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS"
    };

    public static String convertNumberToText(double numero) {
        int parteEntera = (int) numero;

        return convertWholePart(parteEntera) + " PESOS";
    }

    private static String convertWholePart(int parteEntera) {
        if (parteEntera == 0) {
            return "CERO";
        }

        String resultado = "";
        int grupo = 0;

        while (parteEntera > 0) {
            int grupoActual = parteEntera % 1000;
            if (grupoActual > 0) {
                String letrasGrupo = convertGroup(grupoActual);
                resultado = letrasGrupo + " " + getGrupoNombre(grupo) + " " + resultado;
            }
            parteEntera /= 1000;
            grupo++;
        }

        return resultado.trim();
    }

    private static String convertGroup(int grupo) {
        int unidad = grupo % 10;
        int decena = (grupo / 10) % 10;
        int centena = (grupo / 100) % 10;

        String letrasCentena = centena > 0 ? HUNDREDS[centena] : "";
        String letrasDecena = decena > 0 ? TENS[decena] : "";
        String letrasUnidad = unidad > 0 ? UNITS[unidad] : "";

        String resultado = letrasCentena + (letrasCentena.isEmpty() ? "" : " ") +
                letrasDecena + (letrasDecena.isEmpty() ? "" : " Y ") +
                letrasUnidad;

        return resultado.trim();
    }

    private static String getGrupoNombre(int grupo) {
        switch (grupo) {
            case 0:
                return "";
            case 1:
                return "MIL";
            case 2:
                return "MILLÓN";
            case 3:
                return "MIL MILLONES";
            default:
                return ""; // Puedes agregar más nombres para grupos mayores si es necesario
        }
    }

    private static String convertDecimalPart(int parteDecimal) {
        if (parteDecimal == 0) {
            return "CERO";
        } else if (parteDecimal < 10) {
            return UNITS[parteDecimal];
        } else if (parteDecimal < 20) {
            return SPECIALS[parteDecimal - 10];
        } else {
            int unidad = parteDecimal % 10;
            int decena = parteDecimal / 10;
            return TENS[decena] + (unidad > 0 ? " Y " + UNITS[unidad] : "");
        }
    }
}

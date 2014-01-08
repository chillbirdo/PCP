package pcp.tools;

import java.util.List;
import java.util.logging.Logger;
import pcp.PCP;

public class LatexPrinter {

    private static final Logger logger = Logger.getLogger(LatexPrinter.class.getName());
    private static final int OBJ = 0;
    private static final int SD = 1;
    private static final int TIME = 2;

    
    
    public static String toLatexTableStr(String title, List<String> methodList, double[][] tabuIntervals, int[] iterationFactors, List<List<List<Double>>> entryList) {
        int methods = methodList.size();
        int entries = entryList.get(0).size();
        for (List l : entryList) {
            if (entries != l.size()) {
                logger.severe("LATEXPRINTER: different number of results per method.");
            }
        }
        String columnDefinition = "|l|l|";
        for (int i = 0; i < methods; i++) {
            columnDefinition += "|l|l|l|";
        }
        String methodDefinition = "\\multicolumn{2}{|l||}{Parameters}";
        for (int i = 0; i < methodList.size(); i++) {
            String methodStr = methodList.get(i);
            String vertLine = "|l|";
            if (i < methodList.size() - 1) {
                vertLine += "|";
            }
            methodDefinition += "&\\multicolumn{3}{" + vertLine + "}{" + methodStr + "}";
        }
        String columnNames = "ItMax & TabuTenure";
        for (int i = 0; i < methodList.size(); i++) {
            columnNames += " & $\\overline{obj}$ & $sd$ & $\\overline{time}$";
        }

        String entriesStr = new String();
        int iter = 0;
        for (int row = 0; row < entryList.get(0).size(); row++) {
            String itfacStr = "";
            if (row % tabuIntervals.length == 0) {
                if (row > 0) {
                    iter++;
                    entriesStr += "\\hline\\hline\n";
                }
                itfacStr = "\\multirow{" + tabuIntervals.length + "}{*}{" + iterationFactors[iter] + "}";
            }
            //find lowest obj value
            Double lowest = Double.MAX_VALUE;
            for (List entriesForMethod : entryList) {
                List resultForEntry = (List) entriesForMethod.get(row);
                String objStr = cutDigits((Double) resultForEntry.get(OBJ), 2);
                Double obj = Double.parseDouble(objStr);
                if (obj < lowest) {
                    lowest = obj;
                }
            }
            //create entry string
            entriesStr += itfacStr + " & " + "$U[" + tabuIntervals[row % tabuIntervals.length][0] + "C'," + tabuIntervals[row % tabuIntervals.length][1] + "C']$" + " ";
            for (int m = 0; m < entryList.size(); m++) {
                List entriesForMethod = entryList.get(m);
                List resultForEntry = (List) entriesForMethod.get(row);
                String objStr = cutDigits((Double) resultForEntry.get(OBJ), 2);
                Double obj = Double.parseDouble(objStr);
                String sdStr = cutDigits((Double) (resultForEntry.get(SD)), 3);
                String timeStr = cutDigits((Double) (resultForEntry.get(TIME)), 3);
                if (obj <= lowest) {
                    entriesStr += "& \\textbf{" + objStr + "} ";
                } else {
                    entriesStr += "& " + objStr + " ";
                }
                entriesStr += "& " + sdStr + " & " + timeStr + " ";
                if (m == entryList.size() - 1) {
                    entriesStr += "\\\\\n";
                }
            }
        }

        String tableStr = "\\begin{table}[h]\n"
                + "\n"
                + "\\resizebox{\\columnwidth}{!}{%\n"
                + "\\begin{tabular}{" + columnDefinition + "}"
                + "\\hline\n"
                + methodDefinition + "\\\\\n"
                + "\\cline{1-" + (2 + methods * 3) + "}\n"
                + columnNames + "\\\\\n"
                + "\\hline\n"
                + entriesStr
                + "\\hline\n"
                + "\\end{tabular}\n"
                + "}\n"
                + "\\caption{" + title + "}\n"
                + "\\label{tab:" + title + "}"
                + "\\end{table}";

        return tableStr;
    }

    public static String toLatexTable2Str(String title, List<String> methodList, double[][] tabuIntervals, List<List<List<Double>>> entryList, double[] recoloredTabuListSize) {
        int methods = methodList.size();
        int entries = entryList.get(0).size();
        for (List l : entryList) {
            if (entries != l.size()) {
                logger.severe("LATEXPRINTER: different number of results per method.");
            }
        }
        String columnDefinition = "|l|l|";
        for (int i = 0; i < methods; i++) {
            columnDefinition += "|l|l|l|";
        }
        String methodDefinition = "\\multicolumn{2}{|l||}{Parameters}";
        for (int i = 0; i < methodList.size(); i++) {
            String methodStr = methodList.get(i);
            String vertLine = "|l|";
            if (i < methodList.size() - 1) {
                vertLine += "|";
            }
            methodDefinition += "&\\multicolumn{3}{" + vertLine + "}{" + methodStr + "}";
        }
        String columnNames = "RecoloredTT & TabuTenure";
        for (int i = 0; i < methodList.size(); i++) {
            columnNames += " & $\\overline{obj}$ & $sd$ & $\\overline{time}$";
        }

        String entriesStr = new String();
        int iter = 0;
        for (int row = 0; row < entryList.get(0).size(); row++) {
            String itfacStr = "";
            if (row % tabuIntervals.length == 0) {
                if (row > 0) {
                    iter++;
                    entriesStr += "\\hline\\hline\n";
                }
                itfacStr = "\\multirow{" + tabuIntervals.length + "}{*}{" + recoloredTabuListSize[iter] + "}";
            }
            //create entry string
            entriesStr += itfacStr + " & " + "$U[" + tabuIntervals[row % tabuIntervals.length][0] + "C'," + tabuIntervals[row % tabuIntervals.length][1] + "C']$" + " ";
            for (int m = 0; m < entryList.size(); m++) {
                List entriesForMethod = entryList.get(m);
                List resultForEntry = (List) entriesForMethod.get(row);
                String objStr = cutDigits((Double) resultForEntry.get(OBJ), 1);
                Double obj = Double.parseDouble(objStr);
                String sdStr = cutDigits((Double) (resultForEntry.get(SD)), 3);
                String timeStr = cutDigits((Double) (resultForEntry.get(TIME)), 3);
                entriesStr += "& " + objStr + " & " + sdStr + " & " + timeStr + " ";
                if (m == entryList.size() - 1) {
                    entriesStr += "\\\\\n";
                }
            }
        }

        String tableStr = "\\begin{table}[h]\n"
                + "\\textit{" + title + "} \n"
                + "\n"
                + "\\resizebox{\\columnwidth}{!}{%\n"
                + "\\begin{tabular}{" + columnDefinition + "}"
                + "\\hline\n"
                + methodDefinition + "\\\\\n"
                + "\\cline{1-" + (2 + methods * 3) + "}\n"
                + columnNames + "\\\\\n"
                + "\\hline\n"
                + entriesStr
                + "\\hline\n"
                + "\\end{tabular}\n"
                + "}\n"
                + "\\caption{" + title + "}\n"
                + "\\label{tab:" + title + "}"
                + "\\end{table}";

        return tableStr;
    }

    private static String cutDigits(Double d, int digits) {
        String str = String.valueOf(d);
        String[] strSplit = str.split("\\.");
        String beforeDot = strSplit[0];
        String afterDot = strSplit[1];
        if (afterDot.length() < digits) {
            afterDot = String.format("%-" + digits + "s", afterDot).replace(' ', '0');
        } else if (afterDot.length() > digits) {
            afterDot = afterDot.substring(0, digits);
        }
        return beforeDot + "." + afterDot;
    }

    public static String getAlgName(int algConstant) {
        String ret = "";
        switch (algConstant) {
            case PCP.RECOLOR_WITH_ILP:
                ret = "ILP1";
                break;
            case PCP.RECOLOR_WITH_ILP_NOCOLORINGCONSTRAINT:
                ret = "ILP1*";
                break;
            case PCP.RECOLOR_WITH_ILP2:
                ret = "ILP2";
                break;
            case PCP.RECOLOR_WITH_ILP2_NOCOLORINGCONTRAINT:
                ret = "ILP2*";
                break;
            case PCP.RECOLOR_WITH_ONESTEPCD:
                ret = "OneStepCD";
                break;
            case PCP.RECOLOR_WITH_RANDOM:
                ret = "Random";
                break;
        }
        return ret;
    }
}

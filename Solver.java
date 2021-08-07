import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.logging.Level;

public class Solver {
    private static Logger logger = Logger.getLogger(Solver.class.getName());;
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";
    private static final String RESULT = "RESULT";
    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";
    private static final String UNBOUND = "UNBOUND";
    private static final String NEGATE = "NEGATE";
    private static final String UNSURE = "UNSURE";
    private static final String POSITIVE = "POSITIVE";

    public static void main(String args[]) throws IOException {
        String fileName = Arrays.stream(args).filter(x -> x.substring(x.length() - 3, x.length()).equals("txt"))
                .collect(Collectors.toList()).get(0);
        // logger.log(Level.INFO, fileName);
        Boolean convert = Arrays.stream(args).anyMatch(x -> x.equals("-convert"));
        for (int i = 1; i < args.length; i++) {
            if (args[i].contains("txt")) {
                fileName = args[i];
            }
        }
        try {
            CNF cnf = new CNF(fileName, convert);
            Map <String, String> result = dpll1(cnf.getAtoms(), cnf.getter());
            if (result.get(RESULT).equals(FAILURE))
                logger.log(Level.INFO, "Cannot find solution");
            else if (result.get(RESULT).equals(SUCCESS)) {
                result.keySet().forEach( e -> {
                    if (!e.equals(RESULT))
                        System.out.println(e + " = " + result.get(e));
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static Map <String, String> dpll1(List <String> atoms, List <List <String>> set) {
        Map <String, String> v = new HashMap<>();
        atoms.forEach( e -> v.put(e, UNBOUND) );
        return dpll(set, v, atoms);
    }

    static Map <String, String> hasPureLiteral(List <List<String>> set, List<String> atoms) {
        Map<String, String> check = new HashMap<>();
        atoms.forEach(atom -> check.put(atom, UNSURE) );
        set.forEach( e -> {
            e.forEach(a -> {
                if (a.charAt(0) == '!') {
                    String atom = a.substring(1, a.length());
                    if (check.keySet().contains(atom) && check.get(atom).equals(UNSURE)) {
                        check.put(atom, NEGATE);
                    } else if (check.get(atom) != null && check.get(atom).equals(POSITIVE)) {
                        check.remove(atom);
                    }
                } else {
                    if (check.keySet().contains(a) && check.get(a).equals(UNSURE)) {
                        check.put(a, POSITIVE);
                    } else if (check.get(a) != null && check.get(a).equals(NEGATE)) {
                        check.remove(a);
                    }
                }
            });
        });
        check.entrySet().removeIf(entry -> entry.getValue().equals(UNSURE));
        return check;
    }

    static void easyAssign(Map<String, String> assign, Map<String, String> check) {
        // check.keySet().forEach(key -> {
        //     if (check.get(key).equals(NEGATE)) {
        //         if (assign.get(key).equals(UNBOUND)) {
        //             assign.put(key, FALSE);
        //         }
        //     } else {
        //         if (assign.get(key).equals(UNBOUND)) {
        //             assign.put(key, TRUE);
        //         }
        //     }
        // });
        List <String> keyList = new ArrayList<>(check.keySet());
        Collections.sort(keyList);
        String k = keyList.get(0);
        if (check.get(k).equals(NEGATE)) {
            if (assign.get(k).equals(UNBOUND)) {
                assign.put(k, FALSE);
            }
        } else {
            if (assign.get(k).equals(UNBOUND)) {
                assign.put(k, TRUE);
            }
        }

    }

    static void easyAssign(Map <String, String> assign, List<List<String>> simple) {
        for (int i = 0; i < simple.size(); i ++) {
            if (simple.get(i).get(0).charAt(0) == '!') {
                if (assign.get(simple.get(i).get(0).substring(1, simple.get(i).get(0).length())).equals(UNBOUND)) {
                    assign.put(simple.get(i).get(0).substring(1, simple.get(i).get(0).length()), FALSE);
                }
            } else {
                if (assign.get(simple.get(i).get(0)).equals(UNBOUND)) {
                    assign.put(simple.get(i).get(0), TRUE);
                }
            }
        }
        // System.out.println("after assign is "  + assign);
    }

    static void deleteRedundant(List<List<String>> set, Map <String, String> check) {
        List <String> keyList = new ArrayList<>(check.keySet());
        Collections.sort(keyList);
        String k = keyList.get(0);
        // System.out.println("choose " + k + " here");
        int i = 0;
        while (i < set.size()) {
            String toDelete = k;
            if (check.get(k).equals(NEGATE)) toDelete = '!'+k;
            if (set.get(i).contains(toDelete)) {
                set.remove(set.get(i));
                i -= 1;
            }
            i += 1;
        }
        // check.keySet().forEach(key -> {
        //     int i = 0;
        //     while (i < set.size()) {
        //         String toDelete = key;
        //         if (check.get(key).equals(NEGATE)) toDelete = '!'+key;
        //         if (set.get(i).contains(toDelete)) {
        //             set.remove(set.get(i));
        //             i -= 1;
        //         }
        //         i += 1;
        //     }
        // });
        // System.out.println("set is " + set);
    }

    static List <List<String>> propogate(List <List<String>> set, Map <String, String> assign) {
        int i = 0;
        int j = 0;
        List<List<String>> temp = new ArrayList<>();
        set.forEach(e -> 
        {
            List <String> cp = new ArrayList<>();
            cp.addAll(e);
            temp.add(cp);
        });
        while (!temp.isEmpty() && i < temp.size()) {
            List <String> line = temp.get(i);
            Boolean iIncre = true;
            j = 0;
            innerloop:
            while (!temp.get(i).isEmpty() &&  j < temp.get(i).size()) {
                Boolean jIncre = true;
                String each = line.get(j);
                if (each.charAt(0) == '!') {
                    String atom = each.substring(1, each.length());
                    if (assign.get(atom).equals(FALSE)) {
                        iIncre = false;
                        temp.remove(temp.get(i));
                        break innerloop;
                    } else if (assign.get(atom).equals(TRUE)) {
                        temp.get(i).remove(temp.get(i).get(j));
                        // System.out.println(temp);
                        jIncre = false;
                    }
                } else {
                    if (assign.get(each).equals(TRUE)) {
                        temp.remove(temp.get(i));
                        iIncre = false;
                        break innerloop;
                    } else if (assign.get(each).equals(FALSE)) {
                        temp.get(i).remove(temp.get(i).get(j));
                        jIncre = false;
                    }
                }
                if (Boolean.TRUE.equals(jIncre)) j += 1;
                
            }
            if (Boolean.TRUE.equals(iIncre)) i += 1;
        }
        // System.out.println("after removal "+ temp);
        return temp;
    }

    static Map <String, String> dpll(List<List<String>> set, Map <String, String> oAssign, List<String> atoms) {
        Map<String, String> assign = new HashMap<>();
        Map <String, String> check = hasPureLiteral(set, atoms);
        assign.putAll(oAssign);
        List<List<String>> simple = set.stream().filter(e -> e.size() == 1 ).collect(Collectors.toList());
        // System.out.println("here is simple " + simple);
        Boolean isSimple = !simple.isEmpty();
        Boolean isSucc = set.isEmpty();
        Boolean isFail = (set.stream().filter(e -> e.isEmpty()).count() != 0);
        while ( Boolean.TRUE.equals(isSimple) ||
                Boolean.TRUE.equals(isSucc) || 
                Boolean.TRUE.equals(isFail) || 
                !check.isEmpty() ){
            if (Boolean.TRUE.equals(isSucc)) {
                atoms.forEach( e -> {
    //Assign all the unbound with false
                    if (assign.get(e).equals(UNBOUND)) {
                        assign.put(e, FALSE);
                    }
                });
                assign.put(RESULT, SUCCESS);
                return assign;
            } else if (Boolean.TRUE.equals(isFail)) {
                assign.put(RESULT, FAILURE);
                // System.out.println("fail " + assign);
                return assign;
            } else {
                if (Boolean.TRUE.equals(isSimple)) {
                    // System.out.println("simple "+ simple);
                    easyAssign(assign, simple);
                    List<List<String>> temp = new ArrayList<>();
                    set.forEach(e -> 
                    {
                        List <String> cp = new ArrayList<>();
                        cp.addAll(e);
                        temp.add(cp);
                    });
                    set = propogate(temp, assign);
                } else if (!check.isEmpty()) {
                    // System.out.println("check +" + check);
                    easyAssign(assign, check);
                    deleteRedundant(set, check);
                    // System.out.println("assign + " +  assign);
                }
                simple = set.stream().filter(e -> e.size() == 1 ).collect(Collectors.toList());
                isSimple = !simple.isEmpty();
                isSucc = set.isEmpty();
                isFail = (set.stream().filter(e -> e.isEmpty()).count() != 0);
                check = hasPureLiteral(set, atoms);
                // System.out.println(" check is  +" + check);
            }
        }
        List <String> remaining = atoms.stream().filter(atom -> assign.get(atom).equals(UNBOUND))
            .collect(Collectors.toList());
        Collections.sort(remaining);
        Map <String, String> result;
        String e = remaining.get(0);
        assign.put(e, TRUE);
        // System.out.println("hard case: " + e + " : " + TRUE);
        List<List<String>> temp2 = propogate(set, assign);
        // System.out.println("temp2 : " + temp2);
        result = dpll(temp2, assign, atoms);
        if (result.get(RESULT).equals(SUCCESS)) return result;
        assign.remove("result");
        assign.put(e, FALSE);
        // System.out.println("hard case: " + e + " : " + FALSE);
        List<List<String>> temp3 = propogate(set, assign);
        result = dpll(temp3, assign, atoms);
        return result;
    }
}

package service;

public class MultiSelectGenerator {

    public static void main(String[] args) {
        String attrs = "public static volatile SingularAttribute<PraCmdtyRate, PraCmdtyRatePK> id;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, BigDecimal> amcAmt;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, String> cntryCd;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, String> destCntryCd;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, String> destStCd;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, Timestamp> dtlCapxtimestamp;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, String> highDestZipRng;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, String> highZipRng;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, String> lowDestZipRng;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, String> lowZipRng;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, BigDecimal> maxUnitCnt;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, BigDecimal> minUnitCnt;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, String> origStCd;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, String> packagingTypCd;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, BigDecimal> ratePerUnitAmt;\n" +
                "public static volatile SingularAttribute<PraCmdtyRate, Timestamp> replLstUpdtTmst;";

        System.out.println(generateFomSingularity(attrs));
    }

    public static String generateFomSingularity(String singularityAttrbs) {
        if (singularityAttrbs == null || singularityAttrbs.isEmpty()) {
            return null;
        }
        final String[] singAttrs = singularityAttrbs.split("\n");
        String entityName;

        entityName = getEntityName(singAttrs);
        if (entityName == null) return "Please add \\n for each attr";

        StringBuilder sb = new StringBuilder();
        final String entityAttrName = firsToLower(entityName);

        StringBuilder sbMethodBuilder = new StringBuilder();
        sb.append("criteriaQuery.multiselect(");
        String builderMethodName = "build" + entityName;
        sbMethodBuilder.append("private ").append(entityName).append(" " + builderMethodName + "(final Tuple " + entityAttrName + "Tuple) { \n");

        sbMethodBuilder.append("final ").append(entityName).append(" ").append(entityAttrName)
                .append("= new " + entityName + "();\n");

        String entitySingularityClass = entityName + "_";

        for (int i = 0; i < singAttrs.length; i++) {
            String currentAttr = getFieldName(singAttrs[i]);
            String currentDataType = getDataType(singAttrs[i]);

            sb.append("from.get(").append(entitySingularityClass).append(".").append(currentAttr).append(").alias(").append(entitySingularityClass).append(".").append(currentAttr).append(".getName()),\n");
            sbMethodBuilder.append(entityAttrName).append(".set").append(firsToUpper(currentAttr)).append("(").append(entityAttrName).append("Tuple")
                    .append(".get(").append(entitySingularityClass).append(".").append(currentAttr).append(".getName(), ").append(currentDataType)
                    .append(".class));").append("\n");
        }

        String result = sb.toString();
        result = result.substring(0, result.length() - 2);
        result += ")\n" + ".where(cb.and(predicates.toArray(new Predicate[predicates.size()])));\n\n";
        result += "return entityManager\n";
        result += ".createQuery(criteriaQuery)\n";
        result += ".getResultStream()\n";
        result += ".map(this::"+builderMethodName+")\n";
        result += ".collect(Collectors.groupingBy(r -> r.getAttr ));\n}";


        sbMethodBuilder.append("return ").append(entityAttrName).append(";\n}");
        return result + "\n //#### This is the builder ###\n" + sbMethodBuilder.toString();
    }

    private static String getEntityName(String[] singAttrs) {
        String entityName;
        if (singAttrs.length > 0) {
            entityName = singAttrs[0];
            String[] subEnt = entityName.split(",");
            if (subEnt.length > 0) {
                entityName = subEnt[0];
            }
            entityName = entityName.split("<")[1];
            String subEnt2[] = entityName.split(",");
            entityName = subEnt2[0];

        } else {
            return null;
        }
        return entityName;
    }

    private static String getDataType(String attr) {

        if (attr != null) {

            String[] subEnt = attr.split(",");
            if (subEnt.length > 0) {
                attr = subEnt[1];
            }
            attr = attr.split(">")[0].trim();

        } else {
            return null;
        }
        return attr;
    }

    public static String firsToLower(String word) {
        String w1 = word.substring(0, 1).toLowerCase();
        return w1 + word.substring(1, word.length());
    }

    public static String firsToUpper(String word) {
        String w1 = word.substring(0, 1).toUpperCase();
        return w1 + word.substring(1, word.length());
    }

    private static String getFieldName(String singularityAttr) {
        if (singularityAttr != null) {
            if (singularityAttr.contains(" ")) {
                String attrs[] = singularityAttr.split(" ");
                String attr = attrs[attrs.length - 1];
                if (attr.contains(";")) {
                    return attr.replace(";", "");
                } else {
                    return attr;
                }

            }
        }
        return "Attr must looks like this: <PraCmdtyRate, PraCmdtyRatePK> id";
    }

}


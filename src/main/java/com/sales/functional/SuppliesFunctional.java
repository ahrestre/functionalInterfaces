package com.sales.functional;


import com.sales.functional.database.Database;
import com.sales.functional.entities.Product;
import com.sales.functional.entities.Sale;

import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


import java.util.stream.Collectors;

public class SuppliesFunctional {
    static ArrayList<Sale> sales = Database.loadDatabase();
    public static void main(String[] args) {
        loadMenu();

    }

    /** 1. Obtenga todas las ventas(Sale) que tengan como método de compra(Purchase method) 'Online'

        2. Obtenga todas las ventas(Sale) que tengan como ubicación New York y filtre también validando si las ventas fueron con cupón o sin cupón

        3. Obtenga la cantidad de ventas en las que los clientes usaron cupón

        4. Obtenga todas las ventas que fueron realizadas un año específico 'YYYY'

        5. Obtenga el número de ventas en donde el indicador de satisfacción es menor a 4.

        6. Calcule el monto total que pagó el cliente en cada venta.

        7. Obtenga todas las ventas en las que el comprador es una mujer y fue comprado en la tienda ('in store')

        8. Obtenga el número de productos comprados por todos los clientes segmentándolos por etiquetas(tags)

        9. Obtenga cuantos hombres usaron cupón y cuantas mujeres usaron cupón;

        10. Obtenga la venta con la compra más costosa y la venta con la compra más barata
     */

    public static void menu(){
        System.out.println("Supplies sales");
        System.out.println("1. Compras en linea");
        System.out.println("2. Compras realizadas en New York con o sin cupón");
        System.out.println("3. el numero de ventas en donde se usaron cupones y en el numero en las que no");
        System.out.println("4. Ventas realizadas en el año YYYY");
        System.out.println("5. Ventas en donde el indicador de satisfacción es menor a N");
        //TO DO:
        System.out.println("6. Monto total pagado en cada venta");
        System.out.println("7. Ventas en donde compró una mujer en la tienda(in store)");
        System.out.println("8. Agrupación de productos por etiquetas(tags)");
        System.out.println("9. Cuantos hombres y mujeres usaron cupón");
        System.out.println("10. Venta con mayor costo y menor costo");

    }

    public static void loadMenu(){
        Scanner sc = new Scanner(System.in);
        menu();
        System.out.print("Type option: ");
        String op=sc.nextLine();
        switch(op){
            case "1":
                getOnlinePurchases();
                break;
            case "2":
                System.out.print("¿quiere filtrar las ventas que usaron cupón? Y/N: ");
                getNySales(sc.nextLine());
                break;
            case "3":
                couponUsage();
                break;
            case "4":
                System.out.print("Cual es el año por el que quiere filtrar: ");
                salesByYear(sc.nextLine());
                break;
            case "5":
                System.out.print("Cual es el numero de satisfacción por que quiere filtrar (1-5): ");
                salesBySatisfaction(sc.nextLine());
                break;
            case "6":
                getTotalPayment();
                break;
            case "7":
                getWomenInStoreSales();
                break;
            case "8":
                productsByTags();
                break;
            case "9":
                cuponSalesByMenAndWomen();
                break;
            case "10":
                mostExpensiveAndCheapestSale();
                break;
            default:
                System.out.println("ERROR en el input, este metodo no ha sido creado. Intente de nuevo");
        }

    }

    public static void getOnlinePurchases(){
        Predicate<Sale> onlinePurchased = sale -> sale.getPurchasedMethod().equals("Online");
        ArrayList<Sale> result = sales.stream().filter(onlinePurchased).collect(Collectors.toCollection(ArrayList::new));
        result.forEach(System.out::println);

    }

    public static void getNySales(String inCoupon){
        Predicate<Sale> couponUsage = sale -> sale.getCouponUsed().equals(inCoupon.equalsIgnoreCase("Y")) && sale.getLocation().equals("New York");
        ArrayList<Sale> result = sales.stream().filter(couponUsage).collect(Collectors.toCollection(ArrayList::new));
        result.forEach(System.out::println);

    }

    public static void couponUsage(){
        Predicate<Sale> couponUsage = Sale::getCouponUsed;
        Predicate<Sale> couponNoUsage = sale -> !sale.getCouponUsed();
        Map<String,Long> usage  = new HashMap<>(){{
            put("Usage",sales.stream().filter(couponUsage).count());
            put("Not usage",sales.stream().filter(couponNoUsage).count());
        }};

        usage.forEach((key,value)-> System.out.println(key+": "+value));

    }

    public static void salesByYear(String inYear){
        Function<Sale,String> getYear = sale -> String.valueOf(sale.getSaleDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
        ArrayList<Sale> salesByYYYY = sales.stream().filter(sale -> getYear.apply(sale).equals(inYear)).collect(Collectors.toCollection(ArrayList::new));
        salesByYYYY.forEach(System.out::println);
    }

    public static void salesBySatisfaction(String inSatis){
        Consumer<String> satisfaction = satis -> sales.stream().filter(sale -> sale.getCustomer().getSatisfaction().toString().equals(satis)).collect(Collectors.toCollection(ArrayList::new)).forEach(System.out::println);
        satisfaction.accept(inSatis);
    }

    //6. Calcule el monto total que pagó el cliente en cada venta.

    public static void getTotalPayment(){

        Function<List<Product>, Double> getTotal = products -> products.stream().mapToDouble(Product::getPrice).sum();
        sales.forEach(sale -> System.out.println("El pago total es: "+getTotal.apply(sale.getItems())));

    }

    //7. Obtenga todas las ventas en las que el comprador es una mujer y fue comprado en la tienda ('in store')

    public static void getWomenInStoreSales(){

        Predicate<Sale> inStorePurchased = sale -> sale.getPurchasedMethod().equals("In store");
        Predicate<Sale> womenPurchased = sale -> sale.getCustomer().getGender().equals("F") ;

        ArrayList<Sale> result = sales.stream().filter(inStorePurchased).filter(womenPurchased).collect(Collectors.toCollection(ArrayList::new));
        result.forEach(System.out::println);

    }

    //8. Obtenga el número de productos comprados por todos los clientes segmentándolos por etiquetas(tags)

    public static void productsByTags(){

        Map <String, Set<List<String>>> result = new HashMap<>(){};

/*
        Function<Set<List<String>>, String> getTags = products -> products.stream().map(product -> product.getTags()).collect(Collectors.joining(", "));

        sales.forEach(sale -> sale.getItems().forEach(item -> item.getTags().forEach(tag -> {
            (tag, new HashSet<>(){{
                add(item.getTags());
            }});

        } )));

        sales.forEach(sale -> {
            sale.getItems().forEach(product -> {
                product.getTags().forEach(tag -> {
                    if(result.containsKey(tag)){
                        result.get(tag).add(product.getTags());
                    }else{
                        result.put(tag, new HashSet<>(){{
                            add(product.getTags());
                        }});
                    }
                });
            });
        });

 */

        result.forEach((key,value)-> System.out.println(key+": "+value));
    }

    //9. Obtenga cuantos hombres usaron cupón y cuantas mujeres usaron cupón;

    public static void cuponSalesByMenAndWomen(){

        Predicate<Sale> couponUsage = Sale::getCouponUsed;
        Predicate<Sale> womenPurchased = sale -> sale.getCustomer().getGender().equals("F") ;
        Predicate<Sale> menPurchased = sale -> sale.getCustomer().getGender().equals("M") ;

        Map<String,Long> result = new HashMap<>(){{
            put("Number of women who used coupon",sales.stream().filter(couponUsage).filter(womenPurchased).count());
            put("Number of men who used coupon",sales.stream().filter(couponUsage).filter(menPurchased).count());
        }};

        result.forEach((key,value)-> System.out.println(key+": "+value));

        }

    //10. Obtenga la venta con la compra más costosa y la venta con la compra más barata

    public static void mostExpensiveAndCheapestSale(){

        ArrayList<Sale> sorted = sales.stream().sorted(Comparator.comparing(Sale::getTotal)).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Sale> result = new ArrayList<>();

        result.add(sorted.get(0));
        result.add(sorted.get(sorted.size()-1));

        result.forEach(System.out::println);

    }

}

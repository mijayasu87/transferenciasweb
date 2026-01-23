/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author michael
 */
public class IepiFormDep {
    //Producción    
//    public static String USER = "iepi-solicitudes";
//    public static String PASSWORD = "5ad0d5c3fced39d5048f";
//    public static String iepi_formularios = "jdbc:mysql://10.0.20.130:3306/iepi_formularios";   
//    public static String iepi_depurar = "jdbc:mysql://10.0.20.130:3306/iepi_depurar";
//    public static String iepi_casilleros = "jdbc:mysql://10.0.20.130:3306/iepi_casilleros";
//    public static String iepi_admin = "jdbc:mysql://10.0.20.130:3306/iepi_admin";

    //prueba
//    public static String USER = "iepi-solicitudes";
//    public static String PASSWORD = "5ad0d5c3fced39d5048f";
//    public static String iepi_formularios = "jdbc:mysql://10.0.26.130:3306/iepi_formularios";
//    public static String iepi_depurar = "jdbc:mysql://10.0.26.130:3306/iepi_depurar";
//    public static String iepi_casilleros = "jdbc:mysql://10.0.26.130:3306/iepi_casilleros";
//    public static String iepi_admin = "jdbc:mysql://10.0.26.130:3306/iepi_admin";

    //localhost
//    public static String USER = "root";
//    public static String PASSWORD = "";
//    public static String iepi_formularios = "jdbc:mysql://localhost:3306/iepi_formularios";   
//    public static String iepi_depurar = "jdbc:mysql://localhost:3306/iepi_depurar";
//    public static String iepi_casilleros = "jdbc:mysql://localhost:3306/iepi_casilleros";
//    public static String iepi_admin = "jdbc:mysql://localhost:3306/iepi_admin";
    public static Connection doConnectionToFormularios() throws SQLException {
        Connection con = null;
        con = DriverManager.getConnection(Operaciones.iepi_formularios, Operaciones.USER, Operaciones.PASSWORD);
        return con;
    }

    public static Connection doConnectionToDepurar() throws SQLException {
        Connection con = null;
        con = DriverManager.getConnection(Operaciones.iepi_depurar, Operaciones.USER, Operaciones.PASSWORD);
        return con;
    }

    public static Connection doConnectionToCasilleros() throws SQLException {
        Connection con = null;
        con = DriverManager.getConnection(Operaciones.iepi_casilleros, Operaciones.USER, Operaciones.PASSWORD);
        return con;
    }

    public static Connection doConnectionToIepiAdmin() throws SQLException {
        Connection con = null;
        con = DriverManager.getConnection(Operaciones.iepi_admin, Operaciones.USER, Operaciones.PASSWORD);
        return con;
    }
}

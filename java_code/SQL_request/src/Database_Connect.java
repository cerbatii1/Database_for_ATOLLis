import java.sql.*;
import java.util.*;

public class Database_Connect {
    public static void main( String[] args ){
        try{
            Class.forName("org.postgresql.Driver"); //Загрузка JDBC драйвера
        }
        catch (ClassNotFoundException e){
            System.out.println("Driver not found"); //Обработка ошибки его поиска
            e.printStackTrace();
            return;
        }

        final String  DB_URL = "jdbc:postgresql://localhost:5432/postgres";
        final String  DB_USER = "postgres";
        final String  DB_PASS = "rootroot"; //Блок данных БД

        System.out.println("\nSolution selection:" +
                            "\n0. Exit;" +
                            "\n1. Read Database" +
                            "\n2. Add to Database" +
                            "\n3. Remove record from Database" +
                            "\n4. Edit into Database\n");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        switch(choice){
            case 0:
                System.exit(0);

            case 1:
                ReadFromDatabase(DB_URL, DB_USER, DB_PASS);
                break;

            case 2:
                AddToDatabase(DB_URL, DB_USER, DB_PASS);
                break;

            case 3:
                RemoveFromDatabase(DB_URL, DB_USER, DB_PASS);
                break;

            case 4:
                EditDatabase(DB_URL, DB_USER, DB_PASS);
                break;

            default:
                System.out.println("Incorrect input.");
                break;
        }
    }

    public static void ReadFromDatabase(String DB_URL, String DB_USER, String DB_PASS){
        try{
            //Подключение к БД
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            //Создание Statement для выполнения SQL запроса
            Statement statement = connection.createStatement();
            //Формирование SQL запроса для БД
            String sql = "SELECT * FROM ADMINISTRATIVE_UNITS";
            //Выполнение запроса и получение в виде объекта ResulSet
            ResultSet resultSet = statement.executeQuery(sql);

            //Чтение каждой записи из результата запроса
            while (resultSet.next()){
                int unit_id = resultSet.getInt("unit_id");
                String unit_name = resultSet.getString("unit_name");
                int parent_id = resultSet.getInt("parent_id");

                //Вывод на консоль
                System.out.println("Unit ID:" + unit_id + " Unit Name:" + unit_name + " Parent ID:" + parent_id);
            }

            //Закрытие объекта ResulSet,  Statement и соединения с БД
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void AddToDatabase(String DB_URL, String DB_USER, String DB_PASS){
        try{
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the unit ID:");
            int unit_id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Enter the unit name:");
            String unit_name = scanner.nextLine();

            System.out.println("Enter the parent ID:");
            int parent_id = scanner.nextInt();

            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String sql = "INSERT INTO ADMINISTRATIVE_UNITS (unit_id, unit_name, parent_id) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, unit_id);
            preparedStatement.setString(2, unit_name);
            preparedStatement.setInt(3, parent_id);

            //Выполнение SQL запроса на добавление новой записи и получение кол-ва затронутых строк
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0){
                System.out.println("Successfully added to database.");
            }else
            {
                System.out.println("Failed to add the record.");
            }

            //Закрытие объекта PreparedStatement и соединения с базой данных
            preparedStatement.close();
            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void RemoveFromDatabase(String DB_URL, String DB_USER, String DB_PASS){
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the unit ID of the record to remove:");
            int unit_id = scanner.nextInt();

            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String sql = "DELETE FROM ADMINISTRATIVE_UNITS WHERE unit_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, unit_id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Record with Unit ID " + unit_id + " successfully removed from the database.");
            } else {
                System.out.println("No record found with Unit ID " + unit_id + ". Nothing to remove.");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void EditDatabase(String DB_URL, String DB_USER, String DB_PASS){
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the unit ID of the record to edit:");
            int unit_id = scanner.nextInt();

            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String selectSQL = "SELECT * FROM ADMINISTRATIVE_UNITS WHERE unit_id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectSQL);
            selectStatement.setInt(1, unit_id);

            ResultSet resultSet = selectStatement.executeQuery();

            //Проверка, была ли найдена запись с указанным unit_id
            if (resultSet.next()) {
                scanner.nextLine();
                System.out.println("Enter the new unit name:");
                String new_unit_name = scanner.nextLine();

                System.out.println("Enter the new parent ID:");
                int new_parent_id = scanner.nextInt();

                String updateSQL = "UPDATE ADMINISTRATIVE_UNITS SET unit_name = ?, parent_id = ? WHERE unit_id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSQL);
                updateStatement.setString(1, new_unit_name);
                updateStatement.setInt(2, new_parent_id);
                updateStatement.setInt(3, unit_id);

                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Record with Unit ID " + unit_id + " successfully updated in the database.");
                } else {
                    System.out.println("Failed to update the record with Unit ID " + unit_id + ".");
                }

                updateStatement.close();
            } else {
                System.out.println("No record found with Unit ID " + unit_id + ". Nothing to edit.");
            }

            selectStatement.close();
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

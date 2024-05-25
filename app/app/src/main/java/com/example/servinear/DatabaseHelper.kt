import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DatabaseHelper {
    private const val SERVER = "servinear.mysql.database.azure.com"
    private const val DATABASE = "servinear"
    private const val USER = "adminservinear@servinear"
    private const val PASSWORD = "serviNear2024!"

    fun connect(): Connection? {
        return try {
            // Register MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver")

            // Database URL
            val url = "jdbc:mysql://$SERVER:3306/$DATABASE?useSSL=false&serverTimezone=UTC"

            // Establish connection
            val connection = DriverManager.getConnection(url, USER, PASSWORD)
            connection
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }
}

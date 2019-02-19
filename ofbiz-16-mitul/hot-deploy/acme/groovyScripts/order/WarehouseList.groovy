import java.util.*
import java.sql.Timestamp
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.*

module = "WarehouseList.groovy"

warehouseList = null;
	warehouseList = from("Facility").orderBy("facilityId").queryList()





context.warehouseList = warehouseList


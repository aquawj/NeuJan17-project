package com.neu.jan17.search;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.neu.jan17.data.Inventory;
import com.neu.jan17.data.InventoryManager;
import com.neu.jan17.data.Vehicle;

public class SearchTool implements SearchFunc {
		
	private List<Vehicle> data;
	private List<Vehicle> result;
	
	/**
     * Create a searching helper
     *
     * @param folderPath	the path that has all data files   
     */
	public SearchTool(String folderPath) throws FileNotFoundException {
		InventoryManager reader = new InventoryManager(folderPath);
		Map<String, Inventory> vehicleMap = reader.getInventoryMap();
		
		data = new ArrayList<Vehicle>();
		
		flatMapToList(vehicleMap, data);
		
		result = null;
	}
	

	@Override
	public boolean searchByKeyWord(List<Vehicle> base, String kw) {
		result = base.stream().filter(v -> matchKeyWord(v, kw.toLowerCase())).collect(Collectors.toList());
		return result.size() > 0;
	}
	
	@Override
	public boolean searchByKeyWord(String kw) {
		return searchByKeyWord(data, kw);
	}

	@Override
	public boolean searchByFilters(List<Vehicle> base, List<Filter> filters) {
		result = base.stream().filter(v -> matchFilters(v, filters)).collect(Collectors.toList());
		return result.size() > 0;
	}
	
	@Override
	public boolean searchByFilters(List<Filter> filters) {
		return searchByFilters(data, filters);
	}

	@Override
	public List<Vehicle> getData() {
		return data;
	}

	@Override
	public List<Vehicle> getResult() {
		return result == null ? new ArrayList<Vehicle>() : result;
	}

	@Override
	public List<Vehicle> getResult(int pageIndex, int itemsPerPage) {
		int bIdx = pageIndex*itemsPerPage, eIdx = bIdx+itemsPerPage, len = result == null ? 0 : result.size();
		if(bIdx > len - 1) return new ArrayList<Vehicle>();
		return result.subList(bIdx, Math.min(len, eIdx));
	}
	
	
	private boolean matchKeyWord(Vehicle v, String kw) {
		return v.getMake().indexOf(kw) >= 0 || v.getModel().indexOf(kw) >= 0 || v.getBodyType().indexOf(kw) >= 0 || v.getWebId().indexOf(kw) >= 0;
	}
	
	private boolean matchFilters(Vehicle v, List<Filter> filters) {
		return filters.stream().map((f) -> f.matchVehicle(v)).reduce(true, (x, y) -> (x && y));
	}
	
	private void flatMapToList(Map<String, Inventory> vehicleMap, List<Vehicle> data) {
		for (Inventory inventory : vehicleMap.values()) {
		    for(Vehicle v : inventory.getVehicles()) {
		    	data.add(v);
		    }
		}
	}

}

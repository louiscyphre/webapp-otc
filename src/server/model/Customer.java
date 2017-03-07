/*
 *     webapp-otc - an online collaboration tool .
 *     Copyright (C) 2017 Ilia Butvinnik and Michael Goldman
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package server.model;

/**
 * A simple bean to hold data
 */
public class Customer {
	private String Name, City, Country;//customer "schema"
	
	

	public Customer(String name, String city, String country) {
		Name = name;
		City = city;
		Country = country;
	}

	public String getName() {
		return Name;
	}

	public String getCity() {
		return City;
	}

	public String getCountry() {
		return Country;
	}
	
	
}

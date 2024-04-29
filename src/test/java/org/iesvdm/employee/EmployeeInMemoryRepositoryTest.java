package org.iesvdm.employee;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.SystemPropertiesPropertySource;
import org.assertj.core.api.Assertions;
import org.iesvdm.dominio.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test doubles that are "fakes" must be tested
 *
 *
 */
public class EmployeeInMemoryRepositoryTest {

	private EmployeeInMemoryRepository employeeRepository;

	private List<Employee> employees;

	@BeforeEach
	public void setup() {
		employees = new ArrayList<>();
		employeeRepository = new EmployeeInMemoryRepository(employees);
	}

	/**
	 * Descripcion del test:
	 * crea 2 Employee diferentes
	 * aniadelos a la coleccion de employees
	 * comprueba que cuando llamas a employeeRepository.findAll
	 * obtienes los empleados aniadidos en el paso anterior
	 */
	@Test
	public void testEmployeeRepositoryFindAll() {
		Employee empleado1 = new Employee("01", 1200);/** Creo los dos empleados **/
		Employee empleado2 = new Employee("02", 1500);

		employees.add(empleado1);/** Los añado a la coleccion de employees **/
		employees.add(empleado2);

		/** Por ultimo compruebo que cuando llamo a employeeRepository.findAll obtengo los empleados creados **/
		assertThat(employeeRepository.findAll().size()).isEqualTo(2);
		assertThat(employeeRepository.findAll().contains(empleado1) /*&& employeeRepository.findAll().contains(empleado2)*/).isTrue();
		assertThat(employeeRepository.findAll().contains(empleado2)).isTrue();
	}

	/**
	 * Descripcion del test:
	 * salva un Employee mediante el metodo
	 * employeeRepository.save y comprueba que la coleccion
	 * employees contiene solo ese Employee
	 */
	@Test
	public void testEmployeeRepositorySaveNewEmployee() {
		Employee empleado3 = new Employee("03", 3000);/** Creo un empleado**/

		employees.add(empleado3);/** Lo añado a la colección de employees **/

		employeeRepository.save(empleado3); /** Salvo al employee mediante el metodo employeeRepository.save **/

		/** Por último compruebo que el array tiene un sólo elemento y que ese elemento es empleado3 **/
		assertThat( employees.size() == 1 && employees.contains(empleado3)).isTrue();

	}

	/**
	 * Descripcion del tets:
	 * crea un par de Employee diferentes
	 * aniadelos a la coleccion de employees.
	 * A continuacion, mediante employeeRepository.save
	 * salva los Employee anteriores (mismo id) con cambios
	 * en el salario y comprueba que la coleccion employees
	 * los contiene actualizados.
	 */
	@Test
	public void testEmployeeRepositorySaveExistingEmployee() {
		Employee empleado1 = new Employee("01", 1500);/** Cojo los empleados de antes con cambios en el salario **/
		Employee empleado2 = new Employee("02", 1800);
		Employee empleado3 = new Employee("03", 3300);

		employees.add(empleado1);/** Los añado a la colección de employees **/
		employees.add(empleado2);
		employees.add(empleado3);

		employeeRepository.save(empleado1); /** Salvo a los employee mediante el metodo employeeRepository.save **/
		employeeRepository.save(empleado2);
		employeeRepository.save(empleado3);

		/** Por último compruebo que los datos de los empleados se mantienen actualizados **/
		assertThat( empleado1.getSalary() == 1500 && empleado2.getSalary() == 1800 && empleado3.getSalary() == 3300).isTrue();
	}
}

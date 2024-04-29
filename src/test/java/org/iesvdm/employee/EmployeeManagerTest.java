package org.iesvdm.employee;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

public class EmployeeManagerTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private BankService bankService;

	/**
	 * Explica en este comentario que efecto tiene
	 * esta anotacion @InjectMocks: esta anotación , crea el objeto simulado de
	 * la clase e inyecta los objectos simulados que están marcados con la anotación @Mock en él.
	 */
	@InjectMocks
	private EmployeeManager employeeManager;

	@Captor
	private ArgumentCaptor<String> idCaptor;

	@Captor
	private ArgumentCaptor<Double> amountCaptor;

	@Spy
	private Employee notToBePaid = new Employee("1", 1000);

	@Spy
	private Employee toBePaid = new Employee("2", 2000);

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll
	 * que devuelva una coleccion vacia.
	 * Comprueba que al invocar employeeManagar.payEmployees
	 * con el stub anterior no se paga a ningun empleado.
	 */
	@Test
	public void testPayEmployeesReturnZeroWhenNoEmployeesArePresent() {
		/** Compruebo que devuelve una colección vacia **/
		Mockito.when(employeeRepository.findAll()).thenReturn(new ArrayList<>());

		/** Compruebo que no se paga a ningún empleado **/
		assertThat(employeeManager.payEmployees()).isEqualTo(0);

	}

	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn que devuelva una lista con un empleado
	 * para employeeRepository.findAll.
	 * Comprueba que al invocar employeeManager.payEmployess con el stub
	 * anterior se paga a un empleado.
	 * Tambien comprueba con verify que se hace una llamada a bankService.pay
	 * con los datos de pago del Employ del stub when-thenReturn inicialmente
	 * creado.
	 */
	@Test
	public void testPayEmployeesReturnOneWhenOneEmployeeIsPresentAndBankServicePayPaysThatEmployee() {
		/** Me creo un ArrayList con un solo empleado **/
		ArrayList<Employee> empleados = new ArrayList<>();
		Employee employee3 = new Employee("3", 3000);
		empleados.add(employee3);

		/** Compruebo que employeeRepository devuelva una lista con un empleado **/
		Mockito.when(employeeRepository.findAll()).thenReturn(empleados);

		/** Compruebo que se paga a un empleado**/
		assertThat(employeeManager.payEmployees()).isNotEqualTo(0);

		/** Compruebo que se hace una llamada a bankService.pay **/
		verify(bankService).pay(employee3.getId(), employee3.getSalary());

	}


	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll
	 * que devuelva una coleccion con 2 empleados diferentes.
	 * Comprueba que employeeManager.payEmployees paga a 2 empleados.
	 * Verifica la interaccion (con verify) de que se hacen 2 invocaciones
	 * con las caracteristicas de pago de cada Employee que creaste en el stub
	 * primero when-thenReturn.
	 * Por último, verificea que no hay más interacciones con el mock de bankService
	 * -pista verifyNoiMoreInteractions.
	 */
	@Test
	public void testPayEmployeesWhenSeveralEmployeeArePresent() {

		List<Employee> employees = new ArrayList<>();

		Employee empleado1 = new Employee("01", 1200);/** Creo los dos empleados **/
		Employee empleado2 = new Employee("02", 1500);

		employees.add(empleado1);/** Los añado a la coleccion de employees **/
		employees.add(empleado2);

		/** Compruebo que employeeRepository devuelve una colección con dos empleados **/
		Mockito.when(employeeRepository.findAll()).thenReturn(employees);

		/** Compruebo que se paga a los empleados**/
		assertThat(employeeManager.payEmployees()).isNotEqualTo(0);

		/** Verifico la interaccion (con verify) de que se hacen 2 invocaciones con las caracteristicas de pago de cada Employee **/
		verify(bankService).pay(empleado1.getId(), empleado1.getSalary());
		verify(bankService).pay(empleado2.getId(), empleado2.getSalary());

		/** Verifico que no hay más interacciones con el mock de bankService  **/
		verifyNoMoreInteractions(bankService);

	}

	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll
	 * que devuelva una coleccion de 2 empleados.
	 * Comprueba que cuando llamas a employeeManager.payEmployee pagas a 2 empleados.
	 * Para el mock de bankService mediante InOrder e inOrder.verify verifica
	 * que se pagan en orden a los 2 empleados con sus caracteristicas invocando
	 * a pay en el orden de la coleccion.
	 * Por ultimo, verifica que despues de pagar no hay mas interacciones.
	 */
	@Test
	public void testPayEmployeesInOrderWhenSeveralEmployeeArePresent() {
		List<Employee> employees = new ArrayList<>();

		Employee empleado1 = new Employee("01", 1200);/** Creo los dos empleados **/
		Employee empleado2 = new Employee("02", 1500);

		employees.add(empleado1);/** Los añado a la coleccion de employees **/
		employees.add(empleado2);

		/** Compruebo que employeeRepository devuelve una colección con dos empleados **/
		Mockito.when(employeeRepository.findAll()).thenReturn(employees);

		/**  Comprueba que cuando llamas a employeeManager.payEmployee pago a 2 empleados
		 mediante Inorder e inOrder.verify **/
		employeeManager.payEmployees();

		InOrder inOrder = inOrder(bankService);
		inOrder.verify(bankService).pay("01", 1200);
		inOrder.verify(bankService).pay("02", 1500);

		/** Por último, verifico que después de pagar no hay mas interacciones **/
		verifyNoMoreInteractions(bankService);
	}

	/**
	 * Descripcion del test:
	 * Misma situacion que el test anterior solo que al inOrder le aniades tambien employeeRepository
	 * para verificar que antes de hacer el pago bankService.pay para cada empleado
	 * se realiza la invocacion de employeeRepository.findAll.
	 * Pista: utiliza un InOrder inOrder = inOrder(bankService, employeeRepository) para
	 * las verificaciones (verify).
	 */
	@Test
	public void testExampleOfInOrderWithTwoMocks() {
		List<Employee> employees = new ArrayList<>();

		Employee empleado1 = new Employee("01", 1200);/** Creo los dos empleados **/
		Employee empleado2 = new Employee("02", 1500);

		employees.add(empleado1);/** Los añado a la coleccion de employees **/
		employees.add(empleado2);

		/** Verifico que antes de hacer el pago bankService.pay para cada empleado se realiza la invocacion de employeeRepository.findAll  **/
		when(employeeRepository.findAll()).thenReturn(employees);

		employeeManager.payEmployees();

		InOrder inOrder = inOrder(employeeRepository, bankService);
		inOrder.verify(employeeRepository).findAll();
		inOrder.verify(bankService).pay("01", 1200);
		inOrder.verify(bankService).pay("02", 1500);

		/** Por último, verifico que después de pagar no hay mas interacciones **/
		verifyNoMoreInteractions(employeeRepository, bankService);
	}


	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
	 * una coleccion con 2 Employee diferentes. Comprueba que employeesManager.payEmployees paga
	 * a 2 Employee.
	 * Seguidamente utiliza los Captor: idCaptor y amountCaptor para capturar todos los
	 * id's y amounts que se han invocado cuando has comprobado que employManager.payEmployees pagaba a 2,
	 * sobre el mock de bankService en un verify para el metodo pay -puedes aniadir cuantas veces se invoco
	 * al metodo pay en el VerificationMode.
	 * Comprueba los valores de los captor accediendo a ellos mediante captor.getAllValues y comparando
	 * con lo que se espera.
	 * Por ultimo verifica que no hay mas interacciones con el mock de bankService.
	 */
	@Test
	public void testExampleOfArgumentCaptor() {
		List<Employee> employees = new ArrayList<>();

		Employee empleado1 = new Employee("01", 1200);/** Creo los dos empleados **/
		Employee empleado2 = new Employee("02", 1500);

		employees.add(empleado1);/** Los añado a la coleccion de employees **/
		employees.add(empleado2);

		/** Compruebo que employeeRepository devuelve una colección con dos empleados **/
		Mockito.when(employeeRepository.findAll()).thenReturn(employees);

		/**  Comprueba que cuando llamas a employeeManager.payEmployee pago a 2 empleados
		 mediante Inorder e inOrder.verify **/
		employeeManager.payEmployees();

		verify(employeeRepository).findAll();

		/** Luego utilizo los Captor: idCaptor y amountCaptor para capturar todos los id's y amounts **/
		verify(bankService, times(2)).pay(idCaptor.capture(), amountCaptor.capture());

		assertThat(idCaptor.getAllValues()).containsExactly("01", "02");
		assertThat(amountCaptor.getAllValues()).containsExactly(1200.0, 1500.0);

		/** Por último, verifico que después de pagar no hay mas interacciones **/
		verifyNoMoreInteractions(bankService);
	}

	/**
	 * Descripcion del test:
	 * Utiliza el spy toBePaid de los atributos de esta clase de test para
	 * crear un stub when-thenReturn con 1 solo Employee.
	 * Comprueba que al invocar a employeeManager.payEmployees solo paga a 1 Employee.
	 * Por ultimo, mediante un inOrder para 2 mocks: InOrder inOrder = inOrder(bankService, toBePaid)
	 * verifica que la interaccion se realiza en el orden de bankService.pay las caracteristicas
	 * del Employee toBePaid y a continuacion verifica tambien que se invoca toBePaid.setPaid true.
	 */
	@Test
	public void testEmployeeSetPaidIsCalledAfterPaying() {
		List<Employee> employees = new ArrayList<>();

		employees.add(toBePaid);/** Añado el spy toBePaid a la coleccion de employees **/

		/** Compruebo que employeeRepository devuelve una colección con un empleado **/
		Mockito.when(employeeRepository.findAll()).thenReturn(employees);

		employeeManager.payEmployees();

		/** Compruebo que se paga solo a un empleado **/
		verify(bankService, times(1)).pay(idCaptor.capture(), amountCaptor.capture());
		assertThat(idCaptor.getAllValues()).containsExactly("2");
		assertThat(amountCaptor.getAllValues()).containsExactly(2000.0);

		/** Verifico que la interaccion se realiza en el orden de bankService.pay con las caracteristicas del Employee toBePaid **/
		InOrder inOrder = inOrder(bankService, toBePaid);
		inOrder.verify(bankService).pay("2", 2000);

		/** Verifico que se invoca tobePaid.setPaid true **/
		inOrder.verify(toBePaid).setPaid(true);
	}


	/**
	 * Descripcion del test:
	 * Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
	 * una coleccion solo con el spy de atributo de la clase notToBePaid.
	 * Seguidamente, crea un stub doThrow-when para bankService.pay con ArgumentMatcher
	 * any como entradas para el metodo pay. La exception a lanzar sera una RuntimeException
	 * Comprueba que cuando invocas employeeManager.payEmployees con bankService lanzando
	 * una RuntimeException en el stub anterior, los Employee pagados son 0.
	 * Tambien, verifica sobre el spy notToBePaid que se llamo a setPaid false como
	 * efecto de no pago.
	 *
	 */
	@Test
	public void testPayEmployeesWhenBankServiceThrowsException() {
		List<Employee> employees = new ArrayList<>();

		employees.add(notToBePaid);/** Añado el spy toBePaid a la coleccion de employees **/

		/** Compruebo que employeeRepository devuelve una colección con un empleado **/
		Mockito.when(employeeRepository.findAll()).thenReturn(employees);

		/** La exception a lanzar sera una RuntimeException **/
		doThrow(new RuntimeException()).when(bankService).pay(anyString(), anyDouble());

		employeeManager.payEmployees();

		/** Compruebo que se paga solo a un empleado **/
		verify(bankService, times(1)).pay(idCaptor.capture(), amountCaptor.capture());
		assertThat(idCaptor.getAllValues()).containsExactly("1");
		assertThat(amountCaptor.getAllValues()).containsExactly(1000.0);

		/** Verifico que se invoca tobePaid.setPaid false **/
		verify(notToBePaid).setPaid(false);
	}

	/**
	 * Descripcion del test:
	 * 	Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
	 * 	una coleccion 2 Employee con el spy de atributo de la clase notToBePaid y toBePaid.
	 * 	Seguidamente, crea un stub con encademaniento para 2 llamadas doThrow.doNothing-when
	 * 	para bankService.pay de modo que en la primera invocacion de pay (para notToBePaid) se lance una RuntimeException
	 * 	y en la segunda invocacion de pay (para toBePaid) no haga nada. El metodo pay acepta cualquier argumento
	 * 	indicado mediante ArgumentMatcher any.
	 * 	Comprueba que al invocar employeeManager.payEmployees se paga a solo 1 Employee.
	 *  A continuacion, verifica las interacciones (verify) sobre el spy notToBePaid primer mock de la coleccion
	 *  para el que se lanza la RuntimeException y el spy toBePaid segundo mock de la coleccion que si recibe el pago
	 *  chequeando la interaccion con el metodo setPaid a false y true respectivamente.
	 */
	@Test
	public void testOtherEmployeesArePaidWhenBankServiceThrowsException() {
		List<Employee> employees = new ArrayList<>();

		employees.add(notToBePaid);/** Añado el spy notToBePaid a la coleccion de employees **/
		employees.add(toBePaid);/** Añado el spy toBePaid a la coleccion de employees **/

		/** Compruebo que employeeRepository devuelve una colección con los dos empleados **/
		Mockito.when(employeeRepository.findAll()).thenReturn(employees);

		/** Creo un stub con encademaniento para 2 llamadas doThrow.doNothing-when para bankService.pay
		 de modo que en la primera invocacion de pay (para notToBePaid) se lance una RuntimeException
		 y en la segunda invocacion de pay (para toBePaid) no haga nada. **/
		doThrow(new RuntimeException()).doNothing().when(bankService).pay(anyString(), anyDouble());
		employeeManager.payEmployees();
		verify(bankService, times(2)).pay(anyString(), anyDouble());

		/** Verifico las interacciones (verify) sobre el spy notToBePaid primer mock de la coleccion
		para el que lanza la RuntimeException y el spy toBePaid segundo mock de la coleccion **/
		verify(notToBePaid).setPaid(false);
		verify(toBePaid).setPaid(true);

		/** Comprobando que si recibe el pago chequea la interaccion con el metodo setPaid a false y true respectivamente. **/
		assertThat(toBePaid.isPaid()).isTrue();
		assertThat(notToBePaid.isPaid()).isFalse();

	}


	/**
	 * Descripcion del test:
	 * 	Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
	 * 	una coleccion 2 Employee con el spy de atributo de la clase notToBePaid y toBePaid.
	 * 	Seguidamente, crea un stub con encademaniento para 2 llamadas doThrow-when emplea argThat
	 *  argThat(s -> s.equals("1")), anyDouble como firma de invocacion en el stub para pay
	 * 	de modo que en la primera invocacion de pay (para notToBePaid) se lance una RuntimeException
	 * 	y en la segunda invocacion de pay (para toBePaid) no haga nada. El metodo pay acepta cualquier argumento
	 * 	indicado mediante ArgumentMatcher any.
	 * 	Comprueba que al invocar employeeManager.payEmployees se paga a solo 1 Employee.
	 *  A continuacion, verifica las interacciones (verify) sobre el spy notToBePaid primer mock de la coleccion
	 *  para el que se lanza la RuntimeException y el spy toBePaid segundo mock de la coleccion que si recibe el pago
	 *  chequeando la interaccion con el metodo setPaid a false y true respectivamente.
	 */
	@Test
	public void testArgumentMatcherExample() {
		List<Employee> employees = new ArrayList<>();

		employees.add(notToBePaid);/** Añado el spy notToBePaid a la coleccion de employees **/
		employees.add(toBePaid);/** Añado el spy toBePaid a la coleccion de employees **/

		/** Compruebo que employeeRepository devuelve una colección con los dos empleados **/
		Mockito.when(employeeRepository.findAll()).thenReturn(employees);

		/** Creo un stub con encademaniento para 2 llamadas doThrow-when empleando argThat  **/
		Mockito.doThrow(new RuntimeException()).when(bankService).pay(argThat(s -> s.equals("1")), anyDouble());
		Mockito.doNothing().when(bankService).pay(argThat(s -> s.equals("2")), anyDouble());

		employeeManager.payEmployees();

		/** Verifico las interacciones (verify) sobre el spy notToBePaid primer mock de la coleccion
		para el que se lanza la RuntimeException y el spy toBePaid **/
		Mockito.verify(bankService).pay("1", 1000.0);
		Mockito.verify(bankService).pay("2", 2000.0);

		/** Compruebo la interaccion con el metodo setPaid a false y a true respectivamente **/
		Mockito.verify(notToBePaid).setPaid(false);
		Mockito.verify(toBePaid).setPaid(true);

		assertThat(toBePaid.isPaid()).isTrue();
		assertThat(notToBePaid.isPaid()).isFalse();
	}

}

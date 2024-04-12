package org.julleon.manager.controller;

import org.julleon.manager.client.ProductsRestClient;
import org.julleon.manager.client.exception.BadRequestException;
import org.julleon.manager.controller.payload.CreateProductPayload;
import org.julleon.manager.dto.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import java.util.List;

//Mockito Для создания mock-объектов и тестирования методов
//AssertJ Для проверки модели.
//@ExtendWith(MockitoExtension.class) для включения поддержки моков в JUnit 5.
@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductsController")
class ProductsControllerTest {
    @Mock
    private ProductsRestClient productsRestClient;
    @InjectMocks
    private ProductsController productsController;


    @DisplayName("запрос содержит валидный payload  -> метод createProduct создаёт новый товар и возвращает на страницу созданного товара")
    @Test
    void test_createProduct_withRequestValidPayload_thenReturnsRedirectionToProductPage() {
        //given
        //Создаем новый экземпляр класса CreateProductPayload с заданными параметрами
        CreateProductPayload productPayload =
                new CreateProductPayload("Новый товар", "Описание нового товара");
        //Создаем новый экземпляр класса ConcurrentModel
        ConcurrentModel model = new ConcurrentModel();

        //Метод createProduct класса productsRestClient должен возвращать новый экземпляр класса Product с заданными параметрами
        Product toBeReturned = new Product(2, "Новый товар", "Описание нового товара");
//        настройка поведения мок объекта productsRestClient
        Mockito
                .doReturn(toBeReturned)
                .when(this.productsRestClient).createProduct(ArgumentMatchers.isNotNull(CreateProductPayload.class));

        //when
        String result = this.productsController.createProduct(productPayload, model);

        //then
        //Проверяем, что метод createProduct возвращает строку "redirect:/catalogue/products/1"
        Assertions.assertEquals("redirect:/catalogue/products/%d".formatted(toBeReturned.id()), result);

        //Проверяем, что метод createProduct был вызван с заданным экземпляром класса CreateProductPayload
        Mockito.verify(this.productsRestClient).createProduct(productPayload);
        //Проверяем, что больше вызовов метода createProduct не было
        Mockito.verifyNoMoreInteractions(this.productsRestClient);

    }



    @Test
    @DisplayName("запрос содержит невалидный payload -> метод createProduct возвращает на страницу создания товара с списком ошибок")
    void test_createProduct_withRequestInvalidPayload_thenReturnsProductFormWithErrors() {
        //given
        CreateProductPayload productPayload = new CreateProductPayload(" ", null);
        ConcurrentModel model = new ConcurrentModel();
        BadRequestException badRequestException = new BadRequestException(List.of("Ошибка 1", "Ошибка 2"));

//        настройка поведения мок объекта productsRestClient
        Mockito.doThrow(badRequestException)
                .when(this.productsRestClient).createProduct(productPayload);

        //when
        String expectedResult = "catalogue/products/new_product";
        String result = productsController.createProduct(productPayload, model);

//        then
        Assertions.assertEquals(expectedResult, result);
        Assertions.assertEquals(productPayload, model.getAttribute("payload"));
        Assertions.assertEquals(badRequestException.getErrorMessages(), model.getAttribute("errorMessages"));

        Mockito.verify(this.productsRestClient).createProduct(productPayload);
        Mockito.verifyNoMoreInteractions(this.productsRestClient);
    }

}
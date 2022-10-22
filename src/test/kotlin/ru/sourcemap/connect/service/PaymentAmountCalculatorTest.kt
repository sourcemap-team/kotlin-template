//package ru.sourcemap.connect.service
//
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.whenever
//import org.slf4j.LoggerFactory
//import ru.sourcemap.connect.dto.payment.CalculateAmountRequestDto
//import ru.sourcemap.connect.dto.payment.CurrencyDto
//import ru.sourcemap.connect.entity.payment.Currency
//import ru.sourcemap.connect.entity.payment.ExchangeRate
//import ru.sourcemap.connect.repository.CurrencyRepository
//import ru.sourcemap.connect.repository.ExchangeRateRepository
//import java.math.BigDecimal
//import java.math.MathContext
//import java.math.RoundingMode
//
//class PaymentAmountCalculatorTest {
//
//    private val currencyRepository: CurrencyRepository = mock()
//    private val exchangeRateRepository: ExchangeRateRepository = mock()
//    private val currencyService: CurrencyService = mock()
//
//    private val paymentExchangesCalculator = PaymentExchangesCalculator(currencyRepository, exchangeRateRepository, currencyService)
//
//    private val logger = LoggerFactory.getLogger(PaymentAmountCalculatorTest::class.java)
//
//    @BeforeEach
//    fun beforeEach() {
//        val rub = Currency("RUB", null, 2)
//        val usdt = Currency("USDT", null, 5)
//        val usd = Currency("USD", null, 2)
//        whenever(currencyRepository.findByCurrencyInfo_CodeAndCity("RUB", null)).thenReturn(rub)
//        whenever(currencyRepository.findByCurrencyInfo_CodeAndCity("USDT", null)).thenReturn(usdt)
//        whenever(currencyRepository.findByCurrencyInfo_CodeAndCity("USD", null)).thenReturn(usd)
//
//        whenever(exchangeRateRepository.findBySellCurrencyAndBuyCurrency(rub, usdt))
//            .thenReturn(ExchangeRate(rub, usdt, BigDecimal(59.9700).setScale(4, RoundingMode.HALF_EVEN)))
//        whenever(exchangeRateRepository.findBySellCurrencyAndBuyCurrency(usdt, usd))
//            .thenReturn(ExchangeRate(rub, usdt, BigDecimal(1.0000).setScale(4, RoundingMode.HALF_EVEN)))
//        whenever(exchangeRateRepository.findBySellCurrencyAndBuyCurrency(usdt, rub))
//            .thenReturn(ExchangeRate(rub, usdt, BigDecimal(0.0170).setScale(4, RoundingMode.HALF_EVEN)))
//    }
//
//    @Test
//    fun `when payment request is received with sell value then double conversion calculation is correct`() {
//        val paymentRequestDto = CalculateAmountRequestDto(
//            sellCurrency = CurrencyDto(code = "RUB", city = null, decimalPlaces = 2),
//            buyCurrency = CurrencyDto(code = "USD", city = null, decimalPlaces = 2),
//            sellCurrencyAmount = BigDecimal(100),
//            buyCurrencyAmount = null
//        )
//        val result = paymentExchangesCalculator.calculateAmount(paymentRequestDto)
//
//        Assertions.assertEquals(BigDecimal(1.66, MathContext(3, RoundingMode.HALF_EVEN)), result.buyCurrencyAmount)
//        Assertions.assertEquals(null, result.sellCurrencyAmount)
//    }
//
//    @Test
//    fun `when payment request is received with buy value then double conversion calculation is correct`() {
//        val paymentRequestDto = CalculateAmountRequestDto(
//            sellCurrency = CurrencyDto(code = "RUB", city = null, decimalPlaces = 2),
//            buyCurrency = CurrencyDto(code = "USD", city = null, decimalPlaces = 2),
//            sellCurrencyAmount = null,
//            buyCurrencyAmount = BigDecimal(2)
//        )
//        val result = paymentExchangesCalculator.calculateAmount(paymentRequestDto)
//
//        Assertions.assertEquals(BigDecimal(119.94).setScale(2, RoundingMode.HALF_EVEN), result.sellCurrencyAmount)
//        Assertions.assertEquals(null, result.buyCurrencyAmount)
//    }
//
//    @Test
//    fun `when payment request is received with sell value and buy currency USDT then single conversion is correct`() {
//        val paymentRequestDto = CalculateAmountRequestDto(
//            sellCurrency = CurrencyDto(code = "RUB", city = null, decimalPlaces = 2),
//            buyCurrency = CurrencyDto(code = "USDT", city = null, decimalPlaces = 2),
//            sellCurrencyAmount = BigDecimal(100),
//            buyCurrencyAmount = null
//        )
//        val result = paymentExchangesCalculator.calculateAmount(paymentRequestDto)
//
//        Assertions.assertEquals(BigDecimal(1.66750).setScale(5, RoundingMode.HALF_EVEN), result.buyCurrencyAmount)
//        Assertions.assertEquals(null, result.sellCurrencyAmount)
//    }
//
//    @Test
//    fun `when payment request is received with buy value and sell currency USDT then single conversion is correct`() {
//        val paymentRequestDto = CalculateAmountRequestDto(
//            sellCurrency = CurrencyDto(code = "USDT", city = null, decimalPlaces = 2),
//            buyCurrency = CurrencyDto(code = "RUB", city = null, decimalPlaces = 2),
//            sellCurrencyAmount = null,
//            buyCurrencyAmount = BigDecimal(100)
//        )
//        val result = paymentExchangesCalculator.calculateAmount(paymentRequestDto)
//
//        Assertions.assertEquals(BigDecimal(1.70000).setScale(5, RoundingMode.HALF_EVEN), result.sellCurrencyAmount)
//        Assertions.assertEquals(null, result.buyCurrencyAmount)
//    }
//}
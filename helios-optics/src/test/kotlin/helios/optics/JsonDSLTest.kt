package helios.optics

import arrow.core.*
import arrow.instances.option.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.laws.PrismLaws
import arrow.typeclasses.Eq
import helios.core.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class JsonDSLTest : UnitSpec() {

  init {

    testLaws(
      PrismLaws.laws(
        prism = parse(Street.decoder(), Street.encoder()),
        aGen = Gen.json(Street.encoder(), genStreet()),
        bGen = genStreet(),
        funcGen = genFunctionAToB(genStreet()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      )
    )

    "bool prism" {
      forAll(Gen.jsBoolean()) { jsBool ->
        Json.path.boolean.getOption(jsBool) == jsBool.value.some()
      }

      Json.path.boolean.getOption(JsString("false")) shouldBe none<Boolean>()
    }

    "string prism" {
      forAll(Gen.jsString()) { jsString ->
        Json.path.string.getOption(jsString) == jsString.value.some()
      }

      Json.path.string.getOption(JsFalse) shouldBe none<String>()
    }

    "number prism" {
      forAll(Gen.jsNumber()) { jsNumber ->
        Json.path.jsnumber.getOption(jsNumber) == jsNumber.some()
      }

      Json.path.jsnumber.getOption(JsFalse) shouldBe none<JsNumber>()
    }

    "decimal prism" {
      forAll(Gen.jsDecimal()) { jsDecimal ->
        Json.path.decimal.getOption(jsDecimal) == jsDecimal.value.some()
      }

      Json.path.decimal.getOption(JsFalse) shouldBe none<JsDecimal>()
    }

    "long prism" {
      forAll(Gen.jsLong()) { jsLong ->
        Json.path.long.getOption(jsLong) == jsLong.value.some()
      }

      Json.path.long.getOption(JsFalse) shouldBe none<JsLong>()
    }

    "float prism" {
      forAll(Gen.jsFloat()) { jsFloat ->
        Json.path.float.getOption(jsFloat) == jsFloat.value.some()
      }

      Json.path.float.getOption(JsFalse) shouldBe none<JsFloat>()
    }

    "int prism" {
      forAll(Gen.jsInt()) { jsInt ->
        Json.path.int.getOption(jsInt) == jsInt.value.some()
      }

      Json.path.int.getOption(JsString("5")) shouldBe none<Int>()
    }

    "array prism" {
      forAll(Gen.jsArray()) { jsArray ->
        Json.path.array.getOption(jsArray) == jsArray.value.some()
      }

      Json.path.array.getOption(JsString("5")) shouldBe none<JsArray>()
    }

    "object prism" {
      forAll(Gen.jsObject()) { jsObj ->
        Json.path.`object`.getOption(jsObj) == jsObj.value.some()
      }

      Json.path.`object`.getOption(JsString("5")) shouldBe none<JsObject>()
    }

    "null prism" {
      forAll(Gen.jsNull()) { jsNull ->
        Json.path.`null`.getOption(jsNull) == jsNull.some()
      }

      Json.path.`null`.getOption(JsString("5")) shouldBe none<JsNull>()
    }

    "at from object" {
      forAll(Gen.json(City.encoder(), genCity())) { cityJson ->
        Json.path.at("streets").getOption(cityJson).flatMap(::identity) == cityJson["streets"]
      }
    }

    "select from object" {
      forAll(Gen.json(City.encoder(), genCity())) { cityJson ->
        Json.path.select("streets").getOption(cityJson) == cityJson["streets"]
      }
    }

    "extract from object" {
      forAll(Gen.json(City.encoder(), genCity())) { cityJson ->
        Json.path.extract(
          City.decoder(),
          City.encoder()
        ).getOption(cityJson) == City.decoder().decode(cityJson).toOption()
      }
    }

    "get from array" {
      forAll(Gen.json(City.encoder(), genCity())) { cityJson ->
        Json.path.select("streets")[0].extract(Street.decoder(), Street.encoder()).getOption(
          cityJson
        ) ==
            City.decoder().decode(cityJson).toOption().flatMap {
              Option.fromNullable(
                it.streets.getOrNull(
                  0
                )
              )
            }
      }
    }

  }
}
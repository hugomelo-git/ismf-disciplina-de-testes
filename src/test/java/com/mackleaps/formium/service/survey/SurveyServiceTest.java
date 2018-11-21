package com.mackleaps.formium.service.survey;

import com.mackleaps.formium.Application;
import com.mackleaps.formium.exceptions.ComponentNotFoundException;
import com.mackleaps.formium.model.survey.Survey;
import com.mackleaps.formium.repository.survey.SurveyRepository;
import com.mackleaps.formium.repository.survey_application.SurveyResultsRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SurveyServiceTest {

    @Mock
    private SurveyRepository surveyRepository;
    @Mock
    private SurveyResultsRepository surveyResultsRepository;

    private SurveyService surveyService;

    @Before
    public void setup () {
        //SurveyResultsRepository is not used in the current test suite, so its dependency is passed as null
        surveyService = new SurveyService(surveyRepository,null);
    }

    @Test(expected = ComponentNotFoundException.class)
    public void shouldThrowExceptionWhenEditingAndSurveyDoesNotExist () {

        final Long NOT_EXISTING_ID = 5L;

        when(surveyRepository.findOne(NOT_EXISTING_ID)).thenReturn(null);

        Survey survey = new Survey();
        survey.setId(NOT_EXISTING_ID);
        survey.setTitle("New title");
        survey.setDescription("New description");
        survey.setPrefix("New prefix");

        surveyService.editSurvey(survey);
    }

    @Test
    public void shouldReturnSurveyWithEditedValuesIfEverythingWentOkWhenEditing () {

        Long EXISTING_ID = 1L;

        Survey existing = new Survey();
        existing.setPrefix("Prefix");
        existing.setTitle("Title");
        existing.setDescription("Description");
        existing.setId(EXISTING_ID);

        when(surveyRepository.exists(EXISTING_ID)).thenReturn(true);
        when(surveyRepository.saveAndFlush(existing)).thenReturn(existing);

        Survey editedSurvey = surveyService.editSurvey(existing);

        assertEquals(existing.getTitle(), editedSurvey.getTitle());
        assertEquals(existing.getPrefix(), editedSurvey.getPrefix());
        assertEquals(existing.getDescription(), editedSurvey.getDescription());
    }

    @Test(expected = ComponentNotFoundException.class)
    public void shouldThrowExceptionWhenTryingToDeleteANonExistingComponent () {

        Long NOT_EXISTING_SURVEY_ID = 5L;
        when(surveyRepository.exists(NOT_EXISTING_SURVEY_ID)).thenThrow(new ComponentNotFoundException());

        surveyService.deleteSurvey(NOT_EXISTING_SURVEY_ID);
    }

     @Test
    public void shouldAddSurvey(){
        Survey survey = new Survey("José",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin varius egestas libero semper pharetra. Pellentesque nec ipsum ac lectus blandit mattis. " +
                        "Fusce varius diam ut massa feugiat iaculis. Aenean nec commodo elit, ac malesuada tortor. Sed sagittis venenatis leo, et condimentum lectus sollicitudin " +
                        "non. Ut volutpat faucibus ante et venenatis. Sed nec justo ac justo tincidunt interdum id non dui. Nam eget ipsum tincidunt, convallis sapien non, molestie " +
                        "ectus. Suspendisse quis maximus tortor. Nullam eget.",
                "Caso de teste");

        when(surveyRepository.saveAndFlush(survey)).thenReturn(survey);

        Survey surveyIncluded = surveyService.addSurvey(survey);
        assertNotNull(surveyIncluded);
        assertEquals("José", surveyIncluded.getPrefix());
        assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin varius egestas libero semper pharetra. Pellentesque nec ipsum ac lectus blandit mattis. " +
                "Fusce varius diam ut massa feugiat iaculis. Aenean nec commodo elit, ac malesuada tortor. Sed sagittis venenatis leo, et condimentum lectus sollicitudin " +
                "non. Ut volutpat faucibus ante et venenatis. Sed nec justo ac justo tincidunt interdum id non dui. Nam eget ipsum tincidunt, convallis sapien non, molestie " +
                "ectus. Suspendisse quis maximus tortor. Nullam eget.", surveyIncluded.getTitle());
        assertEquals("Caso de teste", surveyIncluded.getDescription());

    }

    @Test
    public void shouldAddSurveyWithNoPrefix(){
        Survey survey = new Survey(null,"Teste","Caso de teste");

        when(surveyRepository.saveAndFlush(survey)).thenReturn(survey);

        Survey surveyIncluded = surveyService.addSurvey(survey);
        assertNotNull(surveyIncluded);
        assertNull(surveyIncluded.getPrefix());
        assertEquals("Teste", surveyIncluded.getTitle());
        assertEquals("Caso de teste", surveyIncluded.getDescription());

    }

    @Test
    public void shouldAddSurveyWithPrefixTenChar(){
        Survey survey = new Survey("teste   10","Teste","Caso de teste");

        when(surveyRepository.saveAndFlush(survey)).thenReturn(survey);

        Survey surveyIncluded = surveyService.addSurvey(survey);
        assertNotNull(surveyIncluded);
        assertEquals("teste   10", surveyIncluded.getPrefix());
        assertEquals("Teste", surveyIncluded.getTitle());
        assertEquals("Caso de teste", surveyIncluded.getDescription());

    }
    
     @Test
    public void shouldAddSurveyWithTitleOneChar(){
        Survey survey = new Survey("teste   10","A","Caso de teste");

        when(surveyRepository.saveAndFlush(survey)).thenReturn(survey);

        Survey surveyIncluded = surveyService.addSurvey(survey);
        assertNotNull(surveyIncluded);
        assertEquals("teste   10", surveyIncluded.getPrefix());
        assertEquals("A", surveyIncluded.getTitle());
        assertEquals("Caso de teste", surveyIncluded.getDescription());

    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionForTryingToAddSurveyWithoutTitle(){
        Survey survey = new Survey("José",null,"Caso de teste");

        when(surveyRepository.saveAndFlush(survey)).thenThrow(NullPointerException.class);

        Survey surveyIncluded = surveyService.addSurvey(survey);
        assertNotNull(surveyIncluded);
        assertEquals("José", surveyIncluded.getPrefix());
        assertNull(surveyIncluded.getTitle());
        assertEquals("Caso de teste", surveyIncluded.getDescription());

    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionForTryingToAddSurveyWithPrefixMoreThenTenChar(){
        Survey survey = new Survey("Mais de 10 caracteres","Teste com mais de 10 Char","Caso de teste");

        when(surveyRepository.saveAndFlush(survey)).thenThrow(RuntimeException.class);

        Survey surveyIncluded = surveyService.addSurvey(survey);
        assertNotNull(surveyIncluded);
        assertEquals("Mais de 10 caracteres", surveyIncluded.getPrefix());
        assertEquals("Teste com mais de 10 Char", surveyIncluded.getTitle());
        assertEquals("Caso de teste", surveyIncluded.getDescription());

    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionForTryingToAddSurveyWithTitleGraterThen300Char(){
        Survey survey = new Survey("Mais de 10 caracteres",
                "UHHUQEDYMITWCTERZNHOVCRMCVGJKHCPWXJKCZLJJZBFTLAJOLYMIIMCHEHPVVZLYXNFZVYWGCHZRENECGHWBSAIGEBVXHWRNEI" +
                        "APXIBXCJHURDGNIYBZYHVHKDTQIXBJTTSAFMBJQQJUGHPRNHWNJAVJREZSEDKDQGZNXZQDMKIHCCUDKKZLQFTNUJCUCVDE" +
                        "QBHHVJGHWZRYAGEEMNWCFDCCRQPNWWTSRVAYLEBOQCTUSSJDJEKLCMVWYYTQGRTBZLFIFNPWWUAUSGAGQOWYJJINFVQKLM" +
                        "VXGXLLXTHBJMWHVEMJQJFSYMZMHXDNZG",
                "Caso de teste");

        when(surveyRepository.saveAndFlush(survey)).thenThrow(RuntimeException.class);

        Survey surveyIncluded = surveyService.addSurvey(survey);
        assertNotNull(surveyIncluded);
        assertEquals("Mais de 10 caracteres", surveyIncluded.getPrefix());
        assertEquals("Caso de teste", surveyIncluded.getDescription());

    }
    
    

}
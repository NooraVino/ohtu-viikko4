package ohtu.verkkokauppa;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KauppaTest {

    Pankki pankki;
    Viitegeneraattori viite;
    Varasto varasto;
    Kauppa k;

    @Before
    public void setUp() {
        pankki = mock(Pankki.class);
        viite = mock(Viitegeneraattori.class);
        varasto = mock(Varasto.class);
        
        
        when(viite.uusi()).thenReturn(42);

        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        
        // sitten testattava kauppa 
        k = new Kauppa(varasto, pankki, viite);

    }

    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {
  
        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(), anyInt());
        // toistaiseksi ei välitetty kutsussa käytetyistä parametreista
    }

    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikeillaParametreilla() {

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");

        //Testataan, että tilisiirtoa kutsutaan oikeallla nimella, tilinrolla ja summalla.
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(5));

    }
    @Test
    public void PankinMetodiaTilisiirtoKutsutaanOikeillaParametreillaKunOstoksiaOnKaksi() {
   
        // määritellään että tuote numero 2 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "juusto", 8));

     
        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "12345");

        //Testataan, että tilisiirtoa kutsutaan oikeallla nimella, tilinrolla ja summalla.
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(13));

    }
    
    @Test
    public void tilisiirtoaKutsutaanOikeinKunKaksiSamaaOstosta() {

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");

        //Testataan, että tilisiirtoa kutsutaan oikeallla nimella, tilinrolla ja summalla.
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(10));

    }
    @Test
    public void TilisiirtoaKutsutaanOikeinKunToisenTuoteenSaldoOnNolla() {

        
        // määritellään että tuote numero 2 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(2)).thenReturn(0);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "juusto", 8));

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "12345");

        //Testataan, että tilisiirtoa kutsutaan oikeallla nimella, tilinrolla ja summalla.
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(5));

    }
    @Test
    public void MetodinAloitaAsiointiLahteeNollasta() {
  

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");
        
        k.aloitaAsiointi();
        k.tilimaksu("maija", "jsdjs");

        //tarkastetaan että ostoskori on tyhjä
        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(), eq(0));

    }
    
    @Test
    public void uusiViiteJokaiselleMaksutapahtumalle() {
   
        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");
        
        k.aloitaAsiointi();
        when(viite.uusi()).thenReturn(55);
        k.tilimaksu("maija", "5677");

        //tarkastetaan että viite on uusi
        verify(pankki).tilisiirto(anyString(), eq(55), anyString(), anyString(), anyInt());

    }
    
    @Test
    public void tuotteenPoistaminenOstoskoristaPalauttaaSEnVarastoon() {
   
    k.poistaKorista(2);
    Tuote t = varasto.haeTuote(2);
    verify(varasto).palautaVarastoon(eq(t));

    }
    
}

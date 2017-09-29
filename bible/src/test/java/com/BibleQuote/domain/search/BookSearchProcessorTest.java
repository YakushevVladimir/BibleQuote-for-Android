/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: BookSearchProcessorTest.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.search;

import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.repository.IModuleRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookSearchProcessorTest {

    private IModuleRepository<String, BaseModule> repository;
    private BaseModule module;
    private String testContent = "<h4>1</h4>\n" +
            "<p><sup>1</sup> В начале сотворил Бог небо и землю.\n" +
            "<p><sup>2</sup> Земля же была безвидна и пуста, и тьма над бездною, и Дух Божий носился над водою.\n" +
            "<p><sup>3</sup> И сказал Бог: да будет свет. И стал свет.\n" +
            "<p><sup>4</sup> И увидел Бог свет, что он хорош, и отделил Бог свет от тьмы.\n" +
            "<p><sup>5</sup> И назвал Бог свет днем, а тьму ночью. И был вечер, и было утро: день один.\n" +
            "<p><sup>6</sup> И сказал Бог: да будет твердь посреди воды, и да отделяет она воду от воды. <font color='#7a8080'>[И стало так.]</font>\n" +
            "<p><sup>7</sup> И создал Бог твердь, и отделил воду, которая под твердью, от воды, которая над твердью. И стало так.\n" +
            "<p><sup>8</sup> И назвал Бог твердь небом. <font color='#7a8080'>[И увидел Бог, что это хорошо.]</font> И был вечер, и было утро: день второй.\n" +
            "<p><sup>9</sup> И сказал Бог: да соберется вода, которая под небом, в одно место, и да явится суша. И стало так. <font color='#7a8080'>[И собралась вода под небом в свои места, и явилась суша.]</font>\n" +
            "<p><sup>10</sup> И назвал Бог сушу землею, а собрание вод назвал морями. И увидел Бог, что это хорошо.\n" +
            "<p><sup>11</sup> И сказал Бог: да произрастит земля зелень, траву, сеющую семя <font color='#7a8080'>[по роду и по подобию ее, и]</font> дерево плодовитое, приносящее по роду своему плод, в котором семя его на земле. И стало так.\n" +
            "<p><sup>12</sup> И произвела земля зелень, траву, сеющую семя по роду <font color='#7a8080'>[и по подобию]</font> ее, и дерево <font color='#7a8080'>[плодовитое]</font>, приносящее плод, в котором семя его по роду его <font color='#7a8080'>[на земле]</font>. И увидел Бог, что это хорошо.\n" +
            "<p><sup>13</sup> И был вечер, и было утро: день третий.\n" +
            "<p><sup>14</sup> И сказал Бог: да будут светила на тверди небесной <font color='#7a8080'>[для освещения земли и]</font> для отделения дня от ночи, и для знамений, и времен, и дней, и годов;\n" +
            "<p><sup>15</sup> и да будут они светильниками на тверди небесной, чтобы светить на землю. И стало так.\n" +
            "<p><sup>16</sup> И создал Бог два светила великие: светило большее, для управления днем, и светило меньшее, для управления ночью, и звезды;\n" +
            "<p><sup>17</sup> и поставил их Бог на тверди небесной, чтобы светить на землю,\n" +
            "<p><sup>18</sup> и управлять днем и ночью, и отделять свет от тьмы. И увидел Бог, что это хорошо.\n" +
            "<p><sup>19</sup> И был вечер, и было утро: день четвёртый.\n" +
            "<p><sup>20</sup> И сказал Бог: да произведет вода пресмыкающихся, душу живую; и птицы да полетят над землею, по тверди небесной. <font color='#7a8080'>[И стало так.]</font>\n" +
            "<p><sup>21</sup> И сотворил Бог рыб больших и всякую душу животных пресмыкающихся, которых произвела вода, по роду их, и всякую птицу пернатую по роду ее. И увидел Бог, что это хорошо.\n" +
            "<p><sup>22</sup> И благословил их Бог, говоря: плодитесь и размножайтесь, и наполняйте воды в морях, и птицы да размножаются на земле.\n" +
            "<p><sup>23</sup> И был вечер, и было утро: день пятый.\n" +
            "<p><sup>24</sup> И сказал Бог: да произведет земля душу живую по роду ее, скотов, и гадов, и зверей земных по роду их. И стало так.\n" +
            "<p><sup>25</sup> И создал Бог зверей земных по роду их, и скот по роду его, и всех гадов земных по роду их. И увидел Бог, что это хорошо.\n" +
            "<p><sup>26</sup> И сказал Бог: сотворим человека по образу Нашему <font color='#7a8080'>[и]</font> по подобию Нашему, и да владычествуют они над рыбами морскими, и над птицами небесными, <font color='#7a8080'>[и над зверями,]</font> и над скотом, и над всею землею, и над всеми гадами, пресмыкающимися по земле.\n" +
            "<p><sup>27</sup> И сотворил Бог человека по образу Своему, по образу Божию сотворил его; мужчину и женщину сотворил их.\n" +
            "<p><sup>28</sup> И благословил их Бог, и сказал им Бог: плодитесь и размножайтесь, и наполняйте землю, и обладайте ею, и владычествуйте над рыбами морскими <font color='#7a8080'>[и над зверями,]</font> и над птицами небесными, <font color='#7a8080'>[и над всяким скотом, и над всею землею,]</font> и над всяким животным, пресмыкающимся по земле.\n" +
            "<p><sup>29</sup> И сказал Бог: вот, Я дал вам всякую траву, сеющую семя, какая есть на всей земле, и всякое дерево, у которого плод древесный, сеющий семя; - вам сие будет в пищу;\n" +
            "<p><sup>30</sup> а всем зверям земным, и всем птицам небесным, и всякому <font color='#7a8080'>[гаду,]</font> пресмыкающемуся по земле, в котором душа живая, дал Я всю зелень травную в пищу. И стало так.\n" +
            "<p><sup>31</sup> И увидел Бог все, что Он создал, и вот, хорошо весьма. И был вечер, и было утро: день шестой.\n" +
            "<h4>2</h4>\n" +
            "<p><sup>1</sup> Так совершены небо и земля и все воинство их.\n" +
            "<p><sup>2</sup> И совершил Бог к седьмому дню дела Свои, которые Он делал, и почил в день седьмый от всех дел Своих, которые делал.\n" +
            "<p><sup>3</sup> И благословил Бог седьмой день, и освятил его, ибо в оный почил от всех дел Своих, которые Бог творил и созидал.\n" +
            "<p><sup>4</sup> Вот происхождение неба и земли, при сотворении их, в то время, когда Господь Бог создал землю и небо,\n" +
            "<p><sup>5</sup> и всякий полевой кустарник, которого еще не было на земле, и всякую полевую траву, которая еще не росла, ибо Господь Бог не посылал дождя на землю, и не было человека для возделывания земли,\n" +
            "<p><sup>6</sup> но пар поднимался с земли и орошал все лице земли.\n" +
            "<p><sup>7</sup> И создал Господь Бог человека из праха земного, и вдунул в лице его дыхание жизни, и стал человек душею живою.\n" +
            "<p><sup>8</sup> И насадил Господь Бог рай в Едеме на востоке, и поместил там человека, которого создал.\n" +
            "<p><sup>9</sup> И произрастил Господь Бог из земли всякое дерево, приятное на вид и хорошее для пищи, и дерево жизни посреди рая, и дерево познания добра и зла.\n" +
            "<p><sup>10</sup> Из Едема выходила река для орошения рая; и потом разделялась на четыре реки.\n" +
            "<p><sup>11</sup> Имя одной Фисон: она обтекает всю землю Хавила, ту, где золото;\n" +
            "<p><sup>12</sup> и золото той земли хорошее; там бдолах и камень оникс.\n" +
            "<p><sup>13</sup> Имя второй реки Гихон <font color='#7a8080'>[Геон]</font>: она обтекает всю землю Куш.\n" +
            "<p><sup>14</sup> Имя третьей реки Хиддекель <font color='#7a8080'>[Тигр]</font>: она протекает пред Ассириею. Четвертая река Евфрат.\n" +
            "<p><sup>15</sup> И взял Господь Бог человека, <font color='#7a8080'>[которого создал,]</font> и поселил его в саду Едемском, чтобы возделывать его и хранить его.\n" +
            "<p><sup>16</sup> И заповедал Господь Бог человеку, говоря: от всякого дерева в саду ты будешь есть,\n" +
            "<p><sup>17</sup> а от дерева познания добра и зла не ешь от него, ибо в день, в который ты вкусишь от него, смертью умрешь.\n" +
            "<p><sup>18</sup> И сказал Господь Бог: не хорошо быть человеку одному; сотворим ему помощника, соответственного ему.\n" +
            "<p><sup>19</sup> Господь Бог образовал из земли всех животных полевых и всех птиц небесных, и привел <font color='#7a8080'>[их]</font> к человеку, чтобы видеть, как он назовет их, и чтобы, как наречет человек всякую душу живую, так и было имя ей.\n" +
            "<p><sup>20</sup> И нарек человек имена всем скотам и птицам небесным и всем зверям полевым; но для человека не нашлось помощника, подобного ему.\n" +
            "<p><sup>21</sup> И навел Господь Бог на человека крепкий сон; и, когда он уснул, взял одно из ребр его, и закрыл то место плотию.\n" +
            "<p><sup>22</sup> И создал Господь Бог из ребра, взятого у человека, жену, и привел ее к человеку.\n" +
            "<p><sup>23</sup> И сказал человек: вот, это кость от костей моих и плоть от плоти моей; она будет называться женою, ибо взята от мужа <font color='#7a8080'>[своего]</font>.\n" +
            "<p><sup>24</sup> Потому оставит человек отца своего и мать свою и прилепится к жене своей; и будут <font color='#7a8080'>[два]</font> одна плоть.\n" +
            "<p><sup>25</sup> И были оба наги, Адам и жена его, и не стыдились.\n" +
            "<h4>3</h4>\n" +
            "<p><sup>1</sup> Змей был хитрее всех зверей полевых, которых создал Господь Бог. И сказал змей жене: подлинно ли сказал Бог: не ешьте ни от какого дерева в раю?\n" +
            "<p><sup>2</sup> И сказала жена змею: плоды с дерев мы можем есть,\n" +
            "<p><sup>3</sup> только плодов дерева, которое среди рая, сказал Бог, не ешьте их и не прикасайтесь к ним, чтобы вам не умереть.\n" +
            "<p><sup>4</sup> И сказал змей жене: нет, не умрете,\n" +
            "<p><sup>5</sup> но знает Бог, что в день, в который вы вкусите их, откроются глаза ваши, и вы будете, как боги, знающие добро и зло.\n" +
            "<p><sup>6</sup> И увидела жена, что дерево хорошо для пищи, и что оно приятно для глаз и вожделенно, потому что дает знание; и взяла плодов его и ела; и дала также мужу своему, и он ел.\n" +
            "<p><sup>7</sup> И открылись глаза у них обоих, и узнали они, что наги, и сшили смоковные листья, и сделали себе опоясания.\n" +
            "<p><sup>8</sup> И услышали голос Господа Бога, ходящего в раю во время прохлады дня; и скрылся Адам и жена его от лица Господа Бога между деревьями рая.\n" +
            "<p><sup>9</sup> И воззвал Господь Бог к Адаму и сказал ему: <font color='#7a8080'>[Адам,]</font> где ты?\n" +
            "<p><sup>10</sup> Он сказал: голос Твой я услышал в раю, и убоялся, потому что я наг, и скрылся.\n" +
            "<p><sup>11</sup> И сказал <font color='#7a8080'>[Бог]</font>: кто сказал тебе, что ты наг? не ел ли ты от дерева, с которого Я запретил тебе есть?\n" +
            "<p><sup>12</sup> Адам сказал: жена, которую Ты мне дал, она дала мне от дерева, и я ел.\n" +
            "<p><sup>13</sup> И сказал Господь Бог жене: что ты это сделала? Жена сказала: змей обольстил меня, и я ела.\n" +
            "<p><sup>14</sup> И сказал Господь Бог змею: за то, что ты сделал это, проклят ты пред всеми скотами и пред всеми зверями полевыми; ты будешь ходить на чреве твоем, и будешь есть прах во все дни жизни твоей;\n" +
            "<p><sup>15</sup> и вражду положу между тобою и между женою, и между семенем твоим и между семенем ее; оно будет поражать тебя в голову, а ты будешь жалить его в пяту * <font color='#7a8080'>[(* По другому чтению: и между Семенем ее; Он будет поражать тебя в голову, а ты будешь жалить Его в пяту. - Прим. ред.)]</font>.\n" +
            "<p><sup>16</sup> Жене сказал: умножая умножу скорбь твою в беременности твоей; в болезни будешь рождать детей; и к мужу твоему влечение твое, и он будет господствовать над тобою.\n" +
            "<p><sup>17</sup> Адаму же сказал: за то, что ты послушал голоса жены твоей и ел от дерева, о котором Я заповедал тебе, сказав: не ешь от него, проклята земля за тебя; со скорбью будешь питаться от нее во все дни жизни твоей;\n" +
            "<p><sup>18</sup> терния и волчцы произрастит она тебе; и будешь питаться полевою травою;\n" +
            "<p><sup>19</sup> в поте лица твоего будешь есть хлеб, доколе не возвратишься в землю, из которой ты взят, ибо прах ты и в прах возвратишься.\n" +
            "<p><sup>20</sup> И нарек Адам имя жене своей: Ева * <font color='#7a8080'>[(* Жизнь. - Прим. ред.)]</font>, ибо она стала матерью всех живущих.\n" +
            "<p><sup>21</sup> И сделал Господь Бог Адаму и жене его одежды кожаные и одел их.\n" +
            "<p><sup>22</sup> И сказал Господь Бог: вот, Адам стал как один из Нас, зная добро и зло; и теперь как бы не простер он руки своей, и не взял также от дерева жизни, и не вкусил, и не стал жить вечно.\n" +
            "<p><sup>23</sup> И выслал его Господь Бог из сада Едемского, чтобы возделывать землю, из которой он взят.\n" +
            "<p><sup>24</sup> И изгнал Адама, и поставил на востоке у сада Едемского Херувима и пламенный меч обращающийся, чтобы охранять путь к дереву жизни.\n" +
            "<h4>4</h4>\n" +
            "<p><sup>1</sup> Адам познал Еву, жену свою; и она зачала, и родила Каина, и сказала: приобрела я человека от Господа.\n" +
            "<p><sup>2</sup> И еще родила брата его, Авеля. И был Авель пастырь овец, а Каин был земледелец.\n" +
            "<p><sup>3</sup> Спустя несколько времени, Каин принес от плодов земли дар Господу,\n" +
            "<p><sup>4</sup> и Авель также принес от первородных стада своего и от тука их. И призрел Господь на Авеля и на дар его,\n" +
            "<p><sup>5</sup> а на Каина и на дар его не призрел. Каин сильно огорчился, и поникло лице его.\n" +
            "<p><sup>6</sup> И сказал Господь <font color='#7a8080'>[Бог]</font> Каину: почему ты огорчился? и отчего поникло лице твое?\n" +
            "<p><sup>7</sup> если делаешь доброе, то не поднимаешь ли лица? а если не делаешь доброго, то у дверей грех лежит; он влечет тебя к себе, но ты господствуй над ним.\n" +
            "<p><sup>8</sup> И сказал Каин Авелю, брату своему: <font color='#7a8080'>[пойдем в поле]</font>. И когда они были в поле, восстал Каин на Авеля, брата своего, и убил его.\n" +
            "<p><sup>9</sup> И сказал Господь <font color='#7a8080'>[Бог]</font> Каину: где Авель, брат твой? Он сказал: не знаю; разве я сторож брату моему?\n" +
            "<p><sup>10</sup> И сказал <font color='#7a8080'>[Господь]</font>: что ты сделал? голос крови брата твоего вопиет ко Мне от земли;\n" +
            "<p><sup>11</sup> и ныне проклят ты от земли, которая отверзла уста свои принять кровь брата твоего от руки твоей;\n" +
            "<p><sup>12</sup> когда ты будешь возделывать землю, она не станет более давать силы своей для тебя; ты будешь изгнанником и скитальцем на земле.\n" +
            "<p><sup>13</sup> И сказал Каин Господу <font color='#7a8080'>[Богу]</font>: наказание мое больше, нежели снести можно;\n" +
            "<p><sup>14</sup> вот, Ты теперь сгоняешь меня с лица земли, и от лица Твоего я скроюсь, и буду изгнанником и скитальцем на земле; и всякий, кто встретится со мною, убьет меня.\n" +
            "<p><sup>15</sup> И сказал ему Господь <font color='#7a8080'>[Бог]</font>: за то всякому, кто убьет Каина, отмстится всемеро. И сделал Господь <font color='#7a8080'>[Бог]</font> Каину знамение, чтобы никто, встретившись с ним, не убил его.\n" +
            "<p><sup>16</sup> И пошел Каин от лица Господня и поселился в земле Нод, на восток от Едема.\n" +
            "<p><sup>17</sup> И познал Каин жену свою; и она зачала и родила Еноха. И построил он город; и назвал город по имени сына своего: Енох.\n" +
            "<p><sup>18</sup> У Еноха родился Ирад <font color='#7a8080'>[Гаидад]</font>; Ирад родил Мехиаеля <font color='#7a8080'>[Малелеила]</font>; Мехиаель родил Мафусала; Мафусал родил Ламеха.\n" +
            "<p><sup>19</sup> И взял себе Ламех две жены: имя одной: Ада, и имя второй: Цилла <font color='#7a8080'>[Селла]</font>.\n" +
            "<p><sup>20</sup> Ада родила Иавала: он был отец живущих в шатрах со стадами.\n" +
            "<p><sup>21</sup> Имя брату его Иувал: он был отец всех играющих на гуслях и свирели.\n" +
            "<p><sup>22</sup> Цилла также родила Тувалкаина <font color='#7a8080'>[Фовела]</font>, который был ковачом всех орудий из меди и железа. И сестра Тувалкаина Ноема.\n" +
            "<p><sup>23</sup> И сказал Ламех женам своим: Ада и Цилла! послушайте голоса моего; жены Ламеховы! внимайте словам моим: я убил мужа в язву мне и отрока в рану мне;\n" +
            "<p><sup>24</sup> если за Каина отмстится всемеро, то за Ламеха в семьдесят раз всемеро.\n" +
            "<p><sup>25</sup> И познал Адам еще <font color='#7a8080'>[Еву,]</font> жену свою, и она родила сына, и нарекла ему имя: Сиф, потому что, <font color='#7a8080'>[говорила она,]</font> Бог положил мне другое семя, вместо Авеля, которого убил Каин.\n" +
            "<p><sup>26</sup> У Сифа также родился сын, и он нарек ему имя: Енос; тогда начали призывать имя Господа <font color='#7a8080'>[Бога]</font>.\n";

    @Before
    public void setUp() throws Exception {
        repository = mock(MockModuleRepository.class);
        when(repository.getBookContent(any(), any())).thenReturn(testContent);
        module = mock(BaseModule.class);
        when(module.getChapterSign()).thenReturn("<h4>");
        when(module.getVerseSign()).thenReturn("<p>");
        when(module.isChapterZero()).thenReturn(false);
        when(module.getID()).thenReturn("RST");
    }

    @Test
    public void searchOneWord() throws Exception {
        BookSearchProcessor<String, BaseModule> searchProcessor = new BookSearchProcessor<>(repository, module, "Gen", "авель");
        Map<String, String> results = searchProcessor.search();

        assertThat(results.size(), equalTo(3));

        Set<String> references = results.keySet();
        assertTrue(references.contains("RST.Gen.4.2"));
        assertTrue(references.contains("RST.Gen.4.4"));
        assertTrue(references.contains("RST.Gen.4.9"));
    }

    @Test
    public void searchTwoWord() throws Exception {
        BookSearchProcessor<String, BaseModule> searchProcessor = new BookSearchProcessor<>(repository, module, "Gen", "авел каин");
        Map<String, String> results = searchProcessor.search();

        assertThat(results.size(), equalTo(3));

        Set<String> references = results.keySet();
        assertTrue(references.contains("RST.Gen.4.2"));
        assertTrue(references.contains("RST.Gen.4.8"));
        assertTrue(references.contains("RST.Gen.4.25"));
    }

    @Test
    public void searchWithEmptyQuery() throws Exception {
        BookSearchProcessor<String, BaseModule> searchProcessor = new BookSearchProcessor<>(repository, module, "Gen", "");
        Map<String, String> results = searchProcessor.search();
        assertNotNull(results);
        assertThat(results.size(), equalTo(0));
    }

}
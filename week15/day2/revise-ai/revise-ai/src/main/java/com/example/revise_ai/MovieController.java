package com.example.revise_ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovieController {

    private final ChatClient chatClient;
    private final ListOutputConverter listOutputConverter;
    private final BeanOutputConverter<Movie> beanOutputConverter;
    private final BeanOutputConverter<List<Movie>> movieListOutputConverter;

    public MovieController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
        this.listOutputConverter = new ListOutputConverter(new DefaultConversionService());
        this.beanOutputConverter = new BeanOutputConverter<>(Movie.class);
        this.movieListOutputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<>() {});
    }

    @GetMapping("/movies")
    public List<String> getMovies(@RequestParam String name) {

        String message = """
                List Top 5 Movies of {name}
                {format}
                """;

        // This is the entire logic using the fluent API:
        return chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(message)
                        .param("name", name)
                        .param("format", listOutputConverter.getFormat())
                )
                .call()
                .entity(listOutputConverter); // 5. Automatically get content AND convert it to a List
    }

    @GetMapping("/movie")
    public Movie getMovieData(@RequestParam String name)
    {
        String message = """
                Get me the best movie of {name}.
                Include the title, year, a brief plotSummary, and a list of mainActors.
                {format}
                """;

//        BeanOutputConverter<Movie> beanOutputConverter = new BeanOutputConverter<>(Movie.class);


         Movie movie = chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(message)
                        .param("name", name)
                        .param("format", beanOutputConverter.getFormat())
                )
                .call()
                .entity(beanOutputConverter);

         return movie;
    }


    @GetMapping("/movieList")
    public List<Movie> getMovieList(@RequestParam String name)
    {

        String message = """
                Get me the Top 5 movies of {name}.
                Include the title, year, a brief plotSummary, and a list of mainActors for each movie.
                {format}
                """;

        List<Movie> movies = chatClient.prompt()
                .user(
                        u->u.text(message)
                                .param("name",name)
                                .param("format",movieListOutputConverter.getFormat())
                        )
                .call()
                .entity(movieListOutputConverter);
        return movies;
    }

}
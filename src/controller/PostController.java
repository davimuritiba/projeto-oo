package controller;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Set;
import model.Post;

public class PostController 
    {
        List <Post> posts;

        public PostController()
            {
                posts = new ArrayList<>();
            }


        public Post createPost(UUID userId, String content, String postType)
            {
                Post post = new Post(userId, content, postType);
                posts.add(post);
                return post;
            }   
            
        public boolean editPost(UUID postId, String newContent, String newPostType) 
            {
                for(Post post : posts)
                    {
                        if(post.getId().equals(postId))
                            {
                                post.setContent(newContent);
                                post.setType(newPostType);
                                return true;
                            }
                    }
                return false;    
            }

            private Post findPostById(UUID postId) 
                {
                    for (Post p : posts) 
                    {
                        if (p.getId().equals(postId)) 
                        {
                            return p;
                        }
                    }
                    return null;
                }
        
            public boolean deletePost(UUID postId)
                {
                    return posts.removeIf(post -> post.getId().equals(postId));
                }
    
            public List<Post> getPostsByUser(UUID userId)
                {
                    List<Post> userPosts = new ArrayList<>();
                    for (Post post : posts)
                        {
                            if (post.getUserId().equals(userId))
                                {
                                    userPosts.add(post);
                                }
                        }
                    return userPosts;
                }
            
            public Post getPostById(UUID postId) 
                {
                    for (Post post : posts) 
                    {
                        if (post.getId().equals(postId)) 
                        {
                            return post;
                        }
                    }
                    return null;
                }
            public boolean likePost(UUID postId, UUID userId) 
                {
                    Post post = findPostById(postId);
                    if (post == null) return false;
        
                    Set<UUID> curtidas = post.getLikes();
                    if (curtidas.contains(userId)) return false;
        
                    curtidas.add(userId);
                    return true;
                }
            public boolean unlikePost(UUID postId, UUID userId) 
                {
                        Post post = findPostById(postId);
                        if (post == null) return false;
            
                        Set<UUID> curtidas = post.getLikes();
                        if (!curtidas.contains(userId)) return false;
            
                        curtidas.remove(userId);
                        return true;
                } 
    
    
    
                public List<Post> getAllPosts()
                    {
                        return new ArrayList<>(posts);
                    }    
    }

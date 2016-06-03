package com.mygdx.ai.functional;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.mygdx.debug.Debugger;


public class Survival implements RoutineSequencialable
{
	 	private final List<RoutineSequencialable> routine;
	    private Queue<RoutineSequencialable> routineQueue;
	    private List<Survivalable> condition;
	     
	    /*
	     * due to the generic nature of this class, it can find use
	     * in other areas such as cutscenes and game scripts
	     */
	    
	    public Survival(List<RoutineSurvivalable> routineList,RoutineSequencialable aspirational) 
	    {
	        routine = new LinkedList<RoutineSequencialable>(routineList);
	        routineQueue = new LinkedList<RoutineSequencialable>();
	        condition = new LinkedList<>();
	    }
	    public static Survival build(RoutineSequencialable aspirational,RoutineSurvivalable... rs)
		{
			List <RoutineSurvivalable> routineList = new LinkedList<>();
			for(int i = 0; i < rs.length; i ++)
			{
				routineList.add(rs[i]);
			}
			return new Survival(routineList,aspirational);
		}
		
		@Override
		public void startSequence() 
		{
			Debugger.tick("Sequence is starting");
			routineQueue.clear();
	        routineQueue.addAll(routine);
	        transverse();
	    	routineQueue.peek().startSequence();   
		}

		@Override
		public void update(float dt) 
		{
			if(routineQueue.peek().succeeded())
			{
				routineQueue.peek().completeSequence();
				routineQueue.poll();
				transverse();
				routineQueue.peek().startSequence();
			}
			else
			{
				routineQueue.peek().update( dt);
			}
		}

		@Override
		public boolean sequenceIsComplete() 
		{
			if(routineQueue.isEmpty())
			{
				return true;
			}
			else if(routineQueue.peek().succeeded())
			{
				return succeededAfterTransverseInstaSucceeded();
			}	
			return false;
		}

		@Override
		public void completeSequence() 
		{
			Debugger.tick("completing Sequence");
			if(!routineQueue.isEmpty())
			{
				routineQueue.peek().completeSequence();
			}
			routineQueue.clear();
		}

		@Override
		public void cancelSequence() 
		{
			routineQueue.peek().cancelSequence();
		}

		@Override
		public boolean succeeded() 
		{
			/*
			 * rewrite a way to check if succeeded
			 */
			//Debugger.tick(routineQueue.peek().getClass().getName());
			if(routineQueue.isEmpty())
			{
				return true;
			}
			else if(routineQueue.peek().succeeded())
			{
				return succeededAfterTransverseInstaSucceeded();
			}
			return false;
		}
		private boolean succeededAfterTransverseInstaSucceeded()
		{
			LinkedList<RoutineSequencialable> copy = new LinkedList<>(routineQueue);
			copy.poll();
			Iterator <RoutineSequencialable> iterator = copy.iterator();
			search:
			while(iterator.hasNext())
			{
				RoutineSequencialable i = iterator.next();
				if(i.instaSucceeded())
				{
					iterator.remove();
				}
				else
				{
					break search;
				}
			}
			return copy.isEmpty();
		}

		@Override
		public boolean failed() 
		{
			if(!conditionUpheld())
			{
				return true;
			}
			else if(routineQueue.isEmpty())
			{
				return false;
			}
			else if(routineQueue.peek().failed())
			{
				return true;
			}
			else if(routineQueue.peek().succeeded())
			{
				return failedAfterTransverseInstaFailed();
			}
			return false;
		}
		
		private boolean conditionUpheld()
		{
			boolean upheld = true;
			for(int i = 0; i < routine.indexOf(routineQueue.peek()); i ++)
			{
				if(!condition.get(i).conditionUpheld())
				{
					upheld = false;
				}
			}
			return upheld;
		}
		
		private boolean failedAfterTransverseInstaFailed()
		{
			boolean ret = false;
			LinkedList<RoutineSequencialable> copy = new LinkedList<>(routineQueue);
			// get rid of the routine that succeeded
			copy.poll();
			search:
			for(int i = 0; i < copy.size(); i ++)
			{
				if(copy.get(i).instaFailed())
				{
					// checking that all preceding routines have ALREADY succeeded
					for(int j = 0; j < i; j++)
					{
						if(!copy.get(j).instaSucceeded())
						{
							ret = false;
							break search;
						}
					}
				ret = true;
				break search;
				}
			}
			return ret;
		}
		
		private void transverse()
		{
			/*
			 * Sifts through all routines in the routineQueue, then determines the first one
			 * that has not succeeded to run. Otherwise, it would take one tick to check 
			 * a routine that has already succeeded instead of instantly moving on to the next one
			 * Must also detect failures
			 * Also returns the list of remaining routineSeq
			 */
			//assert !routineQueue.isEmpty();
			Iterator <RoutineSequencialable> iterator = routineQueue.iterator();
			search:
			while(iterator.hasNext())
			{
				RoutineSequencialable i = iterator.next();
				if(i.instaSucceeded())
				{
					iterator.remove();
				}
				else
				{
					break search;
				}
			}
		}
		public boolean instaSucceeded()
		{
			boolean ret = true;
			for(RoutineSequencialable r : routine)
			{
				if(!r.instaSucceeded())
				{
					ret = false;
				}
			}
			return ret;
		}
		public boolean instaFailed()
		{
			boolean ret = false;
			search:
			for(int i = 0; i < routine.size(); i ++)
			{
				if(routine.get(i).instaFailed())
				{
					// checking that all preceding routines have ALREADY succeeded
					for(int j = 0; j < i; j++)
					{
						if(!routine.get(j).instaSucceeded())
						{
							ret = false;
							break search;
						}
					}
				ret = true;
				break search;
				}
			}
			return ret;
		}
}

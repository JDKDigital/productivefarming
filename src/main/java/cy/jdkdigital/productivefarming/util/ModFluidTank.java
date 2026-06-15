package cy.jdkdigital.productivefarming.util;

import cy.jdkdigital.productivelib.util.MultiFluidTank;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class ModFluidTank extends MultiFluidTank
{
    public ModFluidTank(int capacity) {
        super(1, capacity);
    }

    public ModFluidTank(int tanks, int capacity) {
        super(tanks, capacity);
    }

    public int getTanks() {
        return size();
    }

    public FluidStack getFluidInTank(int tank) {
        return getResource(tank).toStack(getAmountAsInt(tank));
    }

    public FluidStack getFluid() {
        return getFluidInTank(0);
    }

    public int getFluidAmount() {
        return getAmountAsInt(0);
    }

    public void setFluid(FluidStack stack) {
        if (stack.isEmpty()) {
            set(0, FluidResource.EMPTY, 0);
        } else {
            set(0, FluidResource.of(stack), stack.getAmount());
        }
    }

    public int getSpace() {
        return Math.max(0, getCapacity() - totalFluidAmount());
    }

    public boolean isFluidValid(FluidStack stack) {
        return true;
    }

    public int fill(FluidStack resource, boolean execute) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = insert(FluidResource.of(resource), resource.getAmount(), tx);
            if (execute) {
                tx.commit();
            }
            return inserted;
        }
    }

    public FluidStack drain(FluidStack resource, boolean execute) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }
        try (Transaction tx = Transaction.openRoot()) {
            int extracted = extract(FluidResource.of(resource), resource.getAmount(), tx);
            if (execute) {
                tx.commit();
            }
            return extracted > 0 ? resource.copyWithAmount(extracted) : FluidStack.EMPTY;
        }
    }

    public FluidStack drain(int maxDrain, boolean execute) {
        FluidStack current = getFluid();
        if (current.isEmpty() || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }
        return drain(current.copyWithAmount(Math.min(maxDrain, current.getAmount())), execute);
    }

    protected void onContentsChanged() {}

    @Override
    protected void onContentsChanged(int index, FluidStack previousContents) {
        super.onContentsChanged(index, previousContents);
        onContentsChanged();
    }
}
